package com.dao.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author Mamisoa
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PrimaryKey {
    String prefix() default "DEF";
    String sequence() default "sequence";
    int length() default 8;
    boolean autoIncrement() default false; 
}
