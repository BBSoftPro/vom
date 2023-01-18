package io.vom.annotations.actions;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Clear {
    Class<?> returnType() default Void.class;
}
