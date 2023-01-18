package io.vom.utils;

import io.vom.annotations.repositories.Name;
import io.vom.core.Context;
import io.vom.exceptions.SelectorNotFoundException;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import java.io.*;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class SelectorUtils {
    private static final Map<Class<?>, Map<String, Selector>> selectorsMemory = new HashMap<>();

    public static Map<String, Selector> loadCommonSelectors(Context context) {
        try (InputStream inputStream = SelectorUtils.class.getResourceAsStream("/common_selectors.xml")) {
            var selectors = Optional.of(convertXmlToListSelector(context, inputStream, "common_selectors.xml"))
                    .orElse(new HashMap<>());

            File file = new File(FileUtils.getFullPath(Properties.getInstance().getProperty("repository_dir", "repository"))
                    , Properties.getInstance().getProperty("common_selectors_file_name", "common_selectors") + ".xml");

            if (file.isFile()) {
                var userSelectors = convertXmlToListSelector(context, file);
                if (userSelectors != null) {
                    selectors.putAll(userSelectors);
                }
            }

            return selectors;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<String, Selector> findSelectors(Context context, Class<?> klass) {

        var result = selectorsMemory.get(klass);
        if (result == null) {
            result = load(context, klass);
            selectorsMemory.put(klass, result);
        }

        return result;
    }

    public static Selector findSelector(Context context, Object obj, Method method) {
        Objects.requireNonNull(obj);
        Class<?> klass = obj.getClass();
        String matcher = Optional.ofNullable(method.getDeclaredAnnotation(Name.class))
                .map(Name::value)
                .orElse(method.getName());

        while (klass != null) {
            var selectors = findSelectors(context, klass);
            if (selectors == null || selectors.isEmpty()) {
                klass = klass.getSuperclass();
                continue;
            }
            var selector = selectors.get(matcher);

            if (selector == null) {
                klass = klass.getSuperclass();
                continue;
            }

            return selector;
        }

        throw new SelectorNotFoundException("Selector named: " + matcher + " was not found");
    }

    public static Map<String, Selector> load(Context context, Class<?> klass) {
        File file = getClassSelectorsFile(klass);
        return convertXmlToListSelector(context, file);
    }

    public static File getClassSelectorsFile(Class<?> klass) {
        var annotation = klass.getDeclaredAnnotation(Name.class);
        String name;
        if (annotation != null && (!annotation.value().isEmpty())) {
            name = annotation.value();
        } else {
            name = klass.getSimpleName();
        }

        return new File(FileUtils.getFullPath(Properties.getInstance().getProperty("repository_dir", "repository")), name + ".xml");
    }

    public static Map<String, Selector> convertXmlToListSelector(Context context, File file) {
        try {
            return convertXmlToListSelector(context, new FileInputStream(file), file.getName());
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    public static Map<String, Selector> convertXmlToListSelector(Context context, InputStream inputStream, String fileName) {
        SAXBuilder sax = new SAXBuilder();

        try {
            var document = sax.build(inputStream);
            var map = new HashMap<String, Selector>();

            Element rootElement = document.getRootElement();
            rootElement.getChildren("platform")
                    .stream()
                    .filter((element -> Objects.equals(element.getAttribute("name", null).getValue(), context.getDriver().getPlatform())))
                    .findAny().orElseThrow(() -> new IllegalStateException("Platform '" + context.getDriver().getPlatform() + "' was not found on this file: " + fileName))
                    .getChildren("selector")
                    .forEach(element -> {
                        var selector = Selector.from(element.getChild("name").getText()
                                , element.getChild("type").getText()
                                , element.getChild("value").getText()
                                , context.getDriver().getPlatform());
                        var listener = context.getSelectorListener();
                        if (listener != null){
                            selector = listener.onSelectorLoad(selector);
                        }
                        map.put(selector.getName(), selector);
                    });
            return map;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
