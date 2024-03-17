package com.maple.config.core.subscription;

import com.maple.config.core.exp.SmartConfigApplicationException;
import com.maple.config.core.listener.ConfigListener;
import com.maple.config.core.model.ConfigEntity;
import com.maple.config.core.model.ReleaseStatusEnum;
import com.maple.config.core.repository.ConfigRepository;
import com.maple.config.core.utils.ClassUtils;
import com.maple.config.core.utils.Lists;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author maple
 * Created Date: 2024/3/7 21:50
 * Description:
 */

public abstract class AbsConfigSubscription implements ConfigSubscription {

    protected ConfigRepository configRepository;

    protected Map<String, List<Field>> configSubscriberMap = new HashMap<>(16);

    protected Map<String, List<Object>> configSubscriberObjMap = new HashMap<>(16);

    protected List<ConfigListener> configListeners = new ArrayList<>();

    @Override
    public void addSubscription(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            this.addSubscription(field, null);
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
        for (ConfigListener configListener : configListeners) {
            configEntityList.forEach(configEntity -> {
                // todo 为抛出异常，修改时间
                configListener.propertyInject(configEntity, configSubscriberMap.get(configEntity.getKey()));
                configEntity.setStatus(ReleaseStatusEnum.RELEASE.getCode());
            });
            configListener.onChange(configEntityList);
        }
    }

    @Override
    public List<Object> getFocusObjListByKey(String key) {
        return configSubscriberObjMap.get(key);
    }

    @Override
    public ConfigRepository getConfigRepository() {
        return configRepository;
    }

    @Override
    public void refresh(ConfigRepository configRepository) {
        if (this.configRepository == null) {
            this.configRepository = configRepository;
        }
        Map<String, ConfigEntity> configEntityMap = configRepository.resolvedPlaceholdersConfigList()
                .stream()
                .collect(Collectors.toMap(ConfigEntity::getKey, Function.identity()));
        for (Map.Entry<String, List<Field>> entry : configSubscriberMap.entrySet()) {
            String configKey = entry.getKey();
            List<Field> focusFieldList = entry.getValue();

            // 字段上的配置key在配置文件中找不到时，给出空配置实体【字段注解上可能有默认值】
            ConfigEntity configEntity = Optional.ofNullable(configEntityMap.get(configKey)).orElse(new ConfigEntity(configKey));

            configListeners.forEach(configListener -> configListener.propertyInject(configEntity, focusFieldList));
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
            throw new SmartConfigApplicationException(field.getClass() + "." + field.getName() + " configKey is null or empty");
        }
        return configKey;
    }

    protected abstract String doParseKey(Field field);

    protected String resolveAnnotation(Annotation annotation) {
        return ClassUtils.resolveAnnotationKey(annotation);
    }
}
