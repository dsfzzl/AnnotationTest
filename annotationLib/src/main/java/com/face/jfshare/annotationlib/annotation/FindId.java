package com.face.jfshare.annotationlib.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注解类，用于代替findViewById
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.TYPE,ElementType.FIELD})
public @interface FindId {
    int value();
}
