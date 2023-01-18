package io.vom.utils;

import io.vom.exceptions.SelectorNotFoundException;
import io.vom.annotations.actions.Clear;
import io.vom.annotations.actions.Click;
import io.vom.annotations.actions.GetText;
import io.vom.annotations.actions.SetText;
import io.vom.annotations.repositories.Name;
import io.vom.core.Context;
import io.vom.core.Repository;
import io.vom.core.Selector;
import io.vom.core.View;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.InvocationHandlerAdapter;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class Reflection {

    private static final Map<Class<? extends Annotation>, InvocationHandler> actionHandler = new HashMap<>();

    static {
        actionHandler.put(GetText.class, Reflection::invokeGetter);
        actionHandler.put(SetText.class, Reflection::invokeSetter);
        actionHandler.put(Clear.class, Reflection::invokeClearer);
        actionHandler.put(Click.class, Reflection::invokeClicker);
    }


    public static <T extends View<T>> T createPageObject(Context context, Class<T> pClass) throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        var map = new HashMap<Method, InvocationHandler>();

        Arrays.stream(pClass.getMethods()).forEach(method -> {
            var handler = findHandler(method);
            if (handler != null) {
                map.put(method, handler);
            }
        });
        T obj = createProxyObject(pClass, map);
        injectFields(context, obj);
        obj.prepare(context);
        return obj;
    }

    private static void injectFields(Context context, View<?> obj) {
        Class<?> klass = obj.getClass();
        while (klass != null) {
            var className = klass.getSimpleName();
            var r = Repository.findRepository(context, klass);
            Arrays.stream(klass.getDeclaredFields())
                    .peek(field -> field.setAccessible(true))
                    .filter(field -> {
                        try {
                            return field.getType() == Selector.class && field.get(obj) == null;
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .forEach(field -> {
                        String name = Optional.ofNullable(field.getDeclaredAnnotation(Name.class))
                                .map(Name::value)
                                .orElse(field.getName());

                        var found = r.stream().filter(selector -> selector.getName().equals(name))
                                .findAny()
                                .orElseThrow(() -> new SelectorNotFoundException("Selector named: '" + name + "' was not found, Selector holder class is '"+className+"'"));
                        try {
                            field.set(obj, found);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    });
            klass = klass.getSuperclass();
        }
    }


    private static InvocationHandler findHandler(Method method) {
        return Arrays.stream(method.getDeclaredAnnotations())
                .map((a) -> actionHandler.get(a.annotationType()))
                .filter(Objects::nonNull)
                .findAny().orElse(null);
    }


    public static <T> T createProxyObject(Class<T> tClass, Map<Method, ? extends InvocationHandler> map) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        var invocationHandler = new Handler(map);

        return new ByteBuddy()
                .subclass(tClass)
                .method(ElementMatchers.anyOf(map.keySet().toArray(new Method[0])))
                .intercept(InvocationHandlerAdapter.of(invocationHandler))
                .make()
                .load(tClass.getClassLoader())
                .getLoaded()
                .getDeclaredConstructor().newInstance();
    }

    private static Object invokeClearer(Object self, Method method, Object[] objects) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        SetText annotation = method.getDeclaredAnnotation(SetText.class);
        var view = (View<?>) self;
        Selector selector = Repository.findSelector(view.getContext(), view, method);
        view.findElement(selector).clear();

        return getReturn(self, method, annotation);
    }

    private static Object invokeSetter(Object self, Method method, Object[] objects) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        SetText annotation = method.getDeclaredAnnotation(SetText.class);
        var view = (View<?>) self;
        Selector selector = Repository.findSelector(view.getContext(), view, method);

        var text = Objects.requireNonNull(objects[0], method.getName() + "argument is null").toString();
        view.findElement(selector).setText(text);

        return getReturn(self, method, annotation);
    }

    private static Object getReturn(Object self, Method method, SetText annotation) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        if (method.getReturnType().isAssignableFrom(self.getClass())) {
            return self;
        } else if (method.getReturnType() == void.class) {
            return void.class;
        } else {
            if (annotation.returnType() != Void.class && annotation.returnType().isAssignableFrom(method.getReturnType())) {
                return annotation.returnType().getDeclaredConstructor().newInstance();
            } else {
                try {
                    if (annotation.returnType() != Void.class) throw new Exception();
                    return method.getReturnType().getDeclaredConstructor().newInstance();
                } catch (Throwable e) {
                    throw new ClassCastException("Method: '" + method.getName() + "'s return type is different than expected ");
                }
            }
        }
    }


    @SuppressWarnings({"unchecked", "rawtypes", "SuspiciousInvocationHandlerImplementation"})
    private static Object invokeClicker(Object self, Method method, Object[] objects) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        var view = (View<?>) self;
        Selector selector = Repository.findSelector(view.getContext(), view, method);
        view.findElement(selector).click();
        Class<? extends View> returnClass = (Class<? extends View>) method.getReturnType();

        return createPageObject(view.getContext(), returnClass);
    }

    private static Object invokeGetter(Object self, Method method, Object[] objects) {
        var view = (View<?>) self;
        Selector selector = Repository.findSelector(view.getContext(), view, method);
        if (method.getReturnType().isAssignableFrom(String.class)) {
            //noinspection SuspiciousInvocationHandlerImplementation
            return view.findElement(selector).getText();
        } else {
            throw new ClassCastException("Method: " + method.getName() + "'s return type must be String");
        }
    }

    public static class Handler implements InvocationHandler {
        Map<Method, ? extends InvocationHandler> map;

        Handler(Map<Method, ? extends InvocationHandler> map) {
            this.map = map;
        }

        @Override
        public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
            return map.get(method).invoke(o, method, objects);
        }
    }
}
