package com.maple.config.core.listener;

import com.maple.config.core.repository.ConfigRepository;
import com.maple.config.core.spring.SmartConfigSpringContext;
import com.maple.config.core.utils.ClassUtils;
import org.springframework.beans.factory.annotation.Value;

import java.lang.reflect.Field;

/**
 * @author maple
 * Created Date: 2024/3/14 23:18
 * Description:
 */

public class SpringAutoUpdateConfigListener extends AutoUpdateConfigListener {

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
