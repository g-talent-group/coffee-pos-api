package com.coffee.pos.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Retryable {
    int maxAttempts() default 3;
    long delay() default 1000L;
    Class<? extends Throwable>[] retryFor() default {Exception.class};
    Class<? extends Throwable>[] noRetryFor() default {};
}