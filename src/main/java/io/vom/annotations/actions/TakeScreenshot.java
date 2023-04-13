package io.vom.annotations.actions;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TakeScreenshot {
    int waitUntil() default -1;
    Class<?> returnType() default Void.class;
}
