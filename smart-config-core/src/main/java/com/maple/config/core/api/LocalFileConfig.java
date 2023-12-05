package com.maple.config.core.api;

import com.maple.config.core.annotation.SmartValue;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * @author yangfeng
 * @date : 2023/12/1 17:16
 * desc:
 */

public class LocalFileConfig extends AbsSmartConfig {
    @Override
    boolean isRegister(Field field) {
        // 因为在本地配置中，所有的实例对象都是手动创建，没有容器管理，在更新字段时无法获取到实例对象，所以仅支持静态字段
        if (!Modifier.isStatic(field.getModifiers())) {
            return false;
        }
        if (!field.isAnnotationPresent(SmartValue.class)) {
            return false;
        }

        SmartValue annotation = field.getAnnotation(SmartValue.class);
        String value = annotation.value();
        return value != null && !value.isEmpty();
    }

    @Override
    String getKey(Field field) {
        SmartValue annotation = field.getAnnotation(SmartValue.class);
        if (annotation == null) {
            return null;
        }
        String[] split = annotation.value().split(":");
        if (split.length == 0) {
            return annotation.value();
        }
        return split[0];
    }

    @Override
    void setDefaultVal(Field field) {
        SmartValue annotation = field.getAnnotation(SmartValue.class);
        if (annotation == null) {
            return;
        }
        String[] split = annotation.value().split(":");
        if (split.length == 0) {
            return;
        }

        // 默认值
        field.setAccessible(true);
        try {
            field.set(null, split[1]);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean containKey(String key) {
        return false;
    }
}
