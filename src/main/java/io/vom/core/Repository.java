package io.vom.core;

import io.vom.annotations.repositories.Name;
import io.vom.exceptions.SelectorNotFoundException;
import io.vom.utils.FileUtil;
import io.vom.utils.Properties;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class Repository {
    private static final Map<Class<?>, List<Selector>> repositories = new HashMap<>();

    public static List<Selector> findRepository(Context context, Class<?> klass) {

        var result = repositories.get(klass);
        if (result == null) {
            result = load(context, klass);
            repositories.put(klass, result);
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
            var rep = findRepository(context, klass);
            if (rep == null || rep.isEmpty()) {
                klass = klass.getSuperclass();
                continue;
            }

            var opt = rep.stream()
                    .filter((s) -> s.getName().equals(matcher))
                    .findAny();

            if (opt.isEmpty()) {
                klass = klass.getSuperclass();
                continue;
            }

            return opt.get();
        }

        throw new SelectorNotFoundException("Selector named: " + matcher + " was not found");
    }

    public static List<Selector> load(Context context, Class<?> klass) {
        File file = getRepositoryPath(klass);
        return convertXmlToListSelector(context, file);
    }

    public static File getRepositoryPath(Class<?> klass) {
        var annotation = klass.getDeclaredAnnotation(Name.class);
        String name;
        if (annotation != null && (!annotation.value().isEmpty())) {
            name = annotation.value();
        } else {
            name = klass.getSimpleName();
        }

        return new File(FileUtil.getFullPath(Properties.getInstance().getProperty("repository","repository")), name + ".xml");
    }

    public static List<Selector> convertXmlToListSelector(Context context, File file) {
        SAXBuilder sax = new SAXBuilder();

        try {
            var document = sax.build(file);
            Element rootElement = document.getRootElement();
            return rootElement.getChildren("platform")
                    .stream()
                    .filter((element -> Objects.equals(element.getAttribute("name", null).getValue(), context.getDriver().getPlatform())))
                    .findAny().orElseThrow(() -> new IllegalStateException("Platform 'android' was not found on this repository: " + file.getName()))
                    .getChildren("selector")
                    .stream()
                    .map(element -> Selector.from(element.getChild("name").getText()
                            , element.getChild("type").getText()
                            , element.getChild("value").getText()
                            , context.getDriver().getPlatform()))
                    .collect(Collectors.toList());

        } catch (IOException e) {
            return null;
        } catch (JDOMException e) {
            throw new RuntimeException(e);
        }
    }
}
