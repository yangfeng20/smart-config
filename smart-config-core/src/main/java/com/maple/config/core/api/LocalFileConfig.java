package com.maple.config.core.api;

import com.maple.config.core.annotation.SmartValue;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

/**
 * @author yangfeng
 * @date : 2023/12/1 17:16
 * desc:
 */

public class LocalFileConfig extends AbsSmartConfig {
    public LocalFileConfig(boolean descInfer) {
        super(descInfer);
    }

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
    String propertyInject(Field field, String value) {
        Annotation annotation = field.getAnnotation(getFieldAnnotation());
        if (annotation == null) {
            return null;
        }

        String annotationVal;
        try {
            annotationVal = (String) annotation.getClass().getDeclaredMethod("value").invoke(annotation);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        // 字段注解上没有默认值，取本地配置文件的value
        String newValue = value;
        String[] split = annotationVal.split(":");
        if (split.length != 0) {
            // 优先获取字段注解上的值
            newValue = split[1];
        }

        // 默认值
        field.setAccessible(true);
        try {
            field.set(null, newValue);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return newValue;
    }

    @Override
    Class<? extends Annotation> getFieldAnnotation() {
        return SmartValue.class;
    }
}
