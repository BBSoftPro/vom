package io.vom.core;

import io.vom.utils.Reflection;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

public class Context {
    protected Driver driver;

    public Driver getDriver() {
        return driver;
    }

    public <T extends View<T>> T loadView(Class<T> viewClass) {
        try {
            return Reflection.createPageObject(this, viewClass);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static ContextBuilder getBuilder() {
        return new ContextBuilder();
    }

    public static class ContextBuilder {

        private final Context context = new Context();

        private Driver driver;

        public ContextBuilder setDriver(Driver driver) {
            this.driver = driver;

            return this;
        }

        public Context build() {
            Objects.requireNonNull(driver, "Driver is null, you should set Driver to start project");
            context.driver = driver;

            return context;
        }
    }
}
