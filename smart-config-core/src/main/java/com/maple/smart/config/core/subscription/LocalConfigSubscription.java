package com.maple.smart.config.core.subscription;

import com.maple.smart.config.core.annotation.JsonValue;
import com.maple.smart.config.core.annotation.SmartValue;

import java.lang.reflect.Field;
import java.util.Optional;

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
    protected String findFieldAnnotationValue(Field field) {
        SmartValue smartValue = field.getAnnotation(SmartValue.class);
        return smartValue != null ? smartValue.value() : Optional.ofNullable(field.getAnnotation(JsonValue.class))
                .map(JsonValue::value).orElse(null);
    }
}
