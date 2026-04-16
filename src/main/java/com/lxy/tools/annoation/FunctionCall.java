package com.lxy.tools.annoation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface FunctionCall {

    public String name() default "";

    public String description() default "";
}
