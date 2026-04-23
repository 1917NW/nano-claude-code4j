package com.lxy.tools.annoation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ParamProperty {
    String description() default "";
    boolean required() default true;
}
