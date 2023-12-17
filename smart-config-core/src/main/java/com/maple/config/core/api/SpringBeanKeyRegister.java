package com.maple.config.core.api;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author maple
 * Created Date: 2023/12/17 14:02
 * Description:
 */

public class SpringBeanKeyRegister implements BeanPostProcessor {

    private final Map<String, List<String>> beanKeyMap = new ConcurrentHashMap<>();

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> clazz = bean.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            Value annotation = field.getAnnotation(Value.class);
            if (annotation == null){
                continue;
            }

            String value = annotation.value();
            String[] split = value.split(":");
            if (split.length != 0) {
                // 优先获取字段注解上的值
                value = split[1];
            }

            List<String> beanNameList = beanKeyMap.getOrDefault(value, new ArrayList<>());
            beanNameList.add(beanName);
            beanKeyMap.put(value, beanNameList);
        }
        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }


    public Map<String, List<String>> getBeanKeyMap() {
        return beanKeyMap;
    }
}
