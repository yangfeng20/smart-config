package com.maple.smart.config.core.annotation;

import java.lang.annotation.*;

/**
 * 作用同 spring {@code @Value}
 *
 * @author maple
 * @since 2023/12/01
 */


@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface SmartValue {

    String value();
}
