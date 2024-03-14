package com.maple.config.core.subscription;

import com.maple.config.core.annotation.JsonValue;
import com.maple.config.core.annotation.SmartValue;
import com.maple.config.core.exp.SmartConfigApplicationException;
import org.springframework.beans.factory.annotation.Value;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * @author maple
 * Created Date: 2024/3/7 21:50
 * Description:
 */

public class SpringConfigSubscription extends LocalConfigSubscription {

    @Override
    protected boolean focus(Field field) {
        return super.focus(field) || field.isAnnotationPresent(Value.class);
    }

    @Override
    protected String doParseKey(Field field) {

        String key = super.doParseKey(field);
        if (key != null) {
            return key;
        }
        Annotation value = field.getAnnotation(Value.class);
        return resolveAnnotation(value);
    }

}
