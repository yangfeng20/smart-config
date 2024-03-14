package com.maple.config.core.subscription;

import com.maple.config.core.exp.SmartConfigApplicationException;
import com.maple.config.core.listener.ConfigListener;
import com.maple.config.core.model.ConfigEntity;
import com.maple.config.core.repository.ConfigRepository;
import com.maple.config.core.utils.Lists;
import com.maple.config.core.utils.SmartConfigConstant;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

/**
 * @author maple
 * Created Date: 2024/3/7 21:50
 * Description:
 */

public abstract class AbsConfigSubscription implements ConfigSubscription {

    protected Map<String, List<Field>> configSubscriberMap = new HashMap<>(16);

    protected Map<String, List<Object>> configSubscriberObjMap = new HashMap<>(16);

    protected List<ConfigListener> configListeners = new ArrayList<>();

    @Override
    public void addSubscription(Class<?> clazz) {
        // todo 是否初始化赋值，@SmartValue @JsonValue  不需要：@Value
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            this.addSubscription(field);
        }
    }

    @Override
    public void addSubscription(Object object) {
        Class<?> clazz = object.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            this.addSubscription(field, object);
        }
    }


    @Override
    public void addSubscription(Field field, Object targetObj) {
        if (!focus(field)) {
            return;
        }

        String configKey = parseKey(field);
        if (configSubscriberMap.containsKey(configKey)) {
            configSubscriberMap.get(configKey).add(field);
        } else {
            configSubscriberMap.put(configKey, Lists.newArrayList(field));
        }

        configSubscriberObjMap.compute(configKey, (key, targetObjList) -> {
            if (targetObjList != null) {
                targetObjList.add(targetObj);
                return targetObjList;
            }
            return Lists.newArrayList(targetObj);
        });
    }

    @Override
    public void subscribe(List<ConfigEntity> configEntityList) {
        configListeners.forEach(configListener -> {
            configListener.onChange(configEntityList);
        });
    }

    @Override
    public List<Object> getFocusObjListByKey(String key) {
        return null;
    }

    @Override
    public void refresh(ConfigRepository configRepository) {
        Map<String, ConfigEntity> configEntityMap = configRepository.configList().stream().collect(Collectors.toMap(ConfigEntity::getKey, Function.identity()));
        for (Map.Entry<String, List<Field>> entry : configSubscriberMap.entrySet()) {
            String configKey = entry.getKey();
            List<Field> focusFieldList = entry.getValue();
            ConfigEntity configEntity = configEntityMap.get(configKey);

            configListeners.forEach(configListener -> {
                configListener.propertyInject(configEntity, focusFieldList);
            });
        }
    }

    @Override
    public void addListener(ConfigListener configListener) {
        if (configListeners.contains(configListener)) {
            return;
        }
        configListener.setConfigSubscription(this);
        configListeners.add(configListener);
    }

    protected abstract boolean focus(Field field);


    protected String parseKey(Field field) {

        String configKey = doParseKey(field);
        if (configKey == null || configKey.isEmpty()) {
            throw new SmartConfigApplicationException(field.getClass() + "." + field.getName() + "configKey is null or empty");
        }
        return configKey;
    }

    protected abstract String doParseKey(Field field);

    protected String resolveAnnotation(Annotation annotation) {
        if (annotation == null) {
            return null;
        }
        Class<? extends Annotation> annotationClazz = annotation.annotationType();
        String annotationValue;
        try {
            Method annotationValueMethod = annotationClazz.getDeclaredMethod("value");
            annotationValue = (String) annotationValueMethod.invoke(annotation);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new SmartConfigApplicationException(e);
        }

        Matcher matcher = SmartConfigConstant.PLACEHOLDER_PATTERN.matcher(annotationValue);
        if (!matcher.find()) {
            return "";
        }
        String valueText = matcher.group(1);
        String configKey = valueText.split(":")[0];
        // todo 默认值
        System.out.println("value.split(\":\")[1] = " + valueText.split(":")[1]);
        return configKey;
    }
}
