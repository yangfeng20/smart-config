package com.maple.config.core.api;

import org.springframework.beans.factory.annotation.Value;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author yangfeng
 * @date : 2023/12/1 17:17
 * desc:
 */

public class SpringBootConfig extends AbsSmartConfig {

    private Map<String, List<String>> beanKeyNameMap;

    public SpringBootConfig(boolean descInfer) {
        super(descInfer);
    }

    @Override
    public void init(List<String> packagePathList, String localConfigPath) {
        super.init(packagePathList, localConfigPath);

        SpringBeanKeyRegister springBeanKeyRegister = SpringContext.getBean(SpringBeanKeyRegister.class);
        beanKeyNameMap = springBeanKeyRegister.getBeanKeyMap();
    }

    @Override
    boolean isRegister(Field field) {
        Value annotation = field.getAnnotation(Value.class);
        if (annotation == null) {
            return false;
        }
        String value = annotation.value();
        return !value.isEmpty();
    }

    @Override
    String getKey(Field field) {
        Value annotation = field.getAnnotation(Value.class);
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
        String configKey = getKey(field);
        List<Object> waitUpdateFieldBeanList = getBeanByKey(configKey);
        for (Object bean : waitUpdateFieldBeanList) {
            field.setAccessible(true);
            try {
                field.set(bean, value);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        return value;
    }

    @Override
    Class<? extends Annotation> getFieldAnnotation() {
        return Value.class;
    }

    private List<Object> getBeanByKey(String key) {
        if (key == null || key.isEmpty()) {
            return Collections.emptyList();
        }

        if (beanKeyNameMap == null){
            throw new RuntimeException("未初始化beanKey注册器");
        }

        List<String> beanNameList = beanKeyNameMap.get(key);
        if (beanNameList == null || beanNameList.isEmpty()) {
            return Collections.emptyList();
        }

        return beanNameList.stream()
                .map(SpringContext::getBean)
                .collect(Collectors.toList());
    }
}
