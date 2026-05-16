package com.lxy.tools.annoation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ObjectProperty {
    String description() default "";
    boolean required() default true;
    String[] enums() default {};
}
