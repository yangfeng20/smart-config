package com.maple.config.core.annotation;

import java.lang.annotation.*;

/**
 *
 * @author maple
 * @date 2023/12/01
 */


@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface SmartValue {

    String value();
}
