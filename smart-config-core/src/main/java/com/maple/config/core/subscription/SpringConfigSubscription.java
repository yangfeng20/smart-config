package com.maple.config.core.subscription;

import com.maple.config.core.annotation.JsonValue;
import com.maple.config.core.annotation.SmartValue;
import com.maple.config.core.exp.SmartConfigApplicationException;
import com.maple.config.core.repository.ConfigRepository;
import com.maple.config.core.spring.SmartConfigSpringContext;
import com.maple.config.core.utils.ClassUtils;
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

    @Override
    protected boolean isSimpleTypeAnnotation(Field field) {
        return super.isSimpleTypeAnnotation(field) || field.isAnnotationPresent(Value.class);
    }

    @Override
    protected String resolveFieldDefaultValue(Field field) {
        String configValue = super.resolveFieldDefaultValue(field);
        if (configValue != null) return configValue;

        ConfigRepository configRepository = SmartConfigSpringContext.getBean(ConfigRepository.class);
        return ClassUtils.resolveAnnotation(field.getAnnotation(Value.class),
                configRepository::getConfig).getValue();
    }
}
