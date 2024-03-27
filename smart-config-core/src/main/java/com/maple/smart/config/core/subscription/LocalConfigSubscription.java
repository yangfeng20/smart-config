package com.maple.smart.config.core.subscription;

import com.maple.smart.config.core.annotation.JsonValue;
import com.maple.smart.config.core.annotation.SmartValue;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * @author maple
 * @since 2024/3/7 21:50
 * Description:
 */

public class LocalConfigSubscription extends AbsConfigSubscription {

    @Override
    protected boolean focus(Field field) {
        return field.isAnnotationPresent(SmartValue.class) || field.isAnnotationPresent(JsonValue.class);
    }

    @Override
    protected String doParseKey(Field field) {
        Annotation smartValue = field.getAnnotation(SmartValue.class);
        Annotation jsonValue = field.getAnnotation(JsonValue.class);
        if (smartValue != null) {
            return resolveAnnotation(smartValue);
        }
        if (jsonValue != null) {
            return resolveAnnotation(jsonValue);
        }
        return null;
    }
}
