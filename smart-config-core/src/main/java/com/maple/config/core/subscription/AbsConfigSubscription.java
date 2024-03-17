package com.maple.config.core.subscription;

import com.alibaba.fastjson.JSON;
import com.maple.config.core.annotation.JsonValue;
import com.maple.config.core.annotation.SmartValue;
import com.maple.config.core.exp.SmartConfigApplicationException;
import com.maple.config.core.inject.PropertyInject;
import com.maple.config.core.listener.ConfigListener;
import com.maple.config.core.model.ConfigEntity;
import com.maple.config.core.model.ReleaseStatusEnum;
import com.maple.config.core.repository.ConfigRepository;
import com.maple.config.core.utils.ClassUtils;
import com.maple.config.core.utils.Lists;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author maple
 * Created Date: 2024/3/7 21:50
 * Description:
 */

public abstract class AbsConfigSubscription implements ConfigSubscription, PropertyInject {

    protected ConfigRepository configRepository;

    protected List<ConfigListener> configListeners = new ArrayList<>();

    protected Map<String, List<Field>> configSubscriberMap = new HashMap<>(16);

    protected Map<String, List<Object>> configSubscriberObjMap = new HashMap<>(16);


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
        configEntityList.forEach(configEntity -> {
            // todo 为抛出异常，修改时间
            this.propertyInject(configEntity, configSubscriberMap.get(configEntity.getKey()));
            configEntity.setStatus(ReleaseStatusEnum.RELEASE.getCode());
        });

        configListeners.forEach(configListener -> {
            configListener.onChange(configEntityList);
        });
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

            this.propertyInject(configEntity, focusFieldList);
        }
    }

    @Override
    public void addListener(ConfigListener configListener) {
        if (configListeners.contains(configListener)) {
            return;
        }
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


    @Override
    public void propertyInject(ConfigEntity configEntity, List<Field> fieldList) {
        fieldList.forEach(field -> {
            field.setAccessible(true);
            try {
                Object fieldValue = resolveValue(field, configEntity.getValue());
                List<Object> fieldTargetObjList = getFocusObjListByKey(configEntity.getKey());
                for (Object fieldTargetObj : fieldTargetObjList) {
                    field.set(fieldTargetObj, fieldValue);
                }
            } catch (IllegalAccessException e) {
                // todo 日志
                e.printStackTrace();
            }
        });
    }

    protected boolean isSimpleTypeAnnotation(Field field) {
        return field.isAnnotationPresent(SmartValue.class);
    }

    protected String resolveFieldDefaultValue(Field field) {
        String configValue = ClassUtils.resolveAnnotation(field.getAnnotation(SmartValue.class), configRepository::getConfig).getValue();
        return configValue != null ? configValue : ClassUtils.resolveAnnotation(field.getAnnotation(JsonValue.class),
                configRepository::getConfig).getValue();
    }

    protected Object resolveValue(Field field, String configValue) {
        // 配置文件中当前字段key没有对应值，查看字段的直接上是否有默认值
        if (configValue == null) {
            configValue = resolveFieldDefaultValue(field);
        }
        if (configValue == null) {
            throw new SmartConfigApplicationException("todo debug 空数据");
        }

        if (isSimpleTypeAnnotation(field)) {
            Class<?> fieldType = field.getType();

            if (String.class.isAssignableFrom(fieldType)) {
                return configValue;
            }

            if (fieldType.equals(Integer.class) || fieldType.equals(int.class)) {
                return Integer.parseInt(configValue);
            } else if (fieldType.equals(Long.class) || fieldType.equals(long.class)) {
                return Long.parseLong(configValue);
            } else if (fieldType.equals(Double.class) || fieldType.equals(double.class)) {
                return Double.parseDouble(configValue);
            } else if (fieldType.equals(Float.class) || fieldType.equals(float.class)) {
                return Float.parseFloat(configValue);
            } else if (fieldType.equals(Short.class) || fieldType.equals(short.class)) {
                return Short.parseShort(configValue);
            } else if (fieldType.equals(BigDecimal.class)) {
                return new BigDecimal(configValue);
            } else if (fieldType.equals(BigInteger.class)) {
                return new BigInteger(configValue);
            } else {
                throw new IllegalArgumentException("Unsupported Number type: " + fieldType.getName());
            }
        }

        if (field.isAnnotationPresent(JsonValue.class)) {
            Type fieldGenericType = field.getGenericType();
            return JSON.parseObject(configValue, fieldGenericType);
        }

        throw new SmartConfigApplicationException("debug error");
    }
}
