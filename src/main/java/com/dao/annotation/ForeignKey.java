package com.dao.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.dao.annotation.conf.ForeignType;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)

public @interface ForeignKey {
    String mappedBy();
    ForeignType foreignType() default ForeignType.OneToMany;
}