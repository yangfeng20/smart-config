package com.maple.config.core.listener;

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
}
