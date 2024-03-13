package com.maple.config.core.subscription;

import com.maple.config.core.annotation.JsonValue;
import com.maple.config.core.annotation.SmartValue;
import com.maple.config.core.exp.SmartConfigApplicationException;
import com.maple.config.core.repository.ConfigRepository;

import java.lang.reflect.Field;

/**
 * @author maple
 * Created Date: 2024/3/7 21:50
 * Description:
 */

public class LocalConfigSubscription extends AbsConfigSubscription {

    @Override
    protected boolean focus(Field field) {
        return field.isAnnotationPresent(SmartValue.class) || field.isAnnotationPresent(JsonValue.class);
    }

    @Override
    protected String parseKey(Field field) {
        SmartValue smartValue = field.getAnnotation(SmartValue.class);
        JsonValue jsonValue = field.getAnnotation(JsonValue.class);
        if (smartValue != null && jsonValue != null) {
            throw new SmartConfigApplicationException("@SmartValue and @JsonValue cannot exist at the same time");
        }
        if (smartValue != null) {
            String[] split = smartValue.value().split(":", 1);
            if (split.length == 0) {
                return smartValue.value();
            }
            return split[0];
        }

        if (jsonValue != null) {
            String[] split = jsonValue.value().split(":", 1);
            if (split.length == 0) {
                return jsonValue.value();
            }
            return split[0];
        }
        throw new SmartConfigApplicationException("The 【focus】 method fails to properly focus on the field");
    }

}
