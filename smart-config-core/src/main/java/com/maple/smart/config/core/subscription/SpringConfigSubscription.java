package com.maple.smart.config.core.subscription;

import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Value;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * @author maple
 * @since 2024/3/7 21:50
 * Description:
 */

public class SpringConfigSubscription extends LocalConfigSubscription {

    @Override
    protected boolean focus(Field field) {
        return super.focus(field) || field.isAnnotationPresent(Value.class);
    }

    @Override
    protected String findFieldAnnotationValue(Field field) {
        String annotationValue = super.findFieldAnnotationValue(field);
        if (annotationValue != null) {
            return annotationValue;
        }
        return Optional.ofNullable(field.getAnnotation(Value.class)).map(Value::value).orElse(null);
    }

    @Override
    protected List<Field> configurationWrapperField(Object bean) {
        Class<?> clazz = bean.getClass();
        boolean aopProxy = AopUtils.isAopProxy(bean);
        boolean containsCglibProxyStr = clazz.getSimpleName().contains("$$EnhancerBySpringCGLIB$$");
        if (aopProxy && !containsCglibProxyStr) {
            return Arrays.asList(bean.getClass().getDeclaredFields());
        }
        Class<?>[] implInterfaces = bean.getClass().getInterfaces();
        if (implInterfaces.length > 0 &&
                implInterfaces[0].getName().equals("org.springframework.context.annotation.ConfigurationClassEnhancer$EnhancedConfiguration")) {
            // 配置类 @Configuration
            return Arrays.asList(clazz.getSuperclass().getDeclaredFields());
        }

        return Arrays.asList(bean.getClass().getDeclaredFields());


    }

    @Override
    protected boolean isSimpleTypeAnnotation(Field field) {
        return super.isSimpleTypeAnnotation(field) || field.isAnnotationPresent(Value.class);
    }
}
