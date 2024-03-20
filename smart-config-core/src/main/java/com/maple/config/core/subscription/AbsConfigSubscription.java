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
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;

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
 * @since 2024/3/7 21:50
 * Description:
 */

public abstract class AbsConfigSubscription implements ConfigSubscription, PropertyInject {

    @Setter
    @Getter
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
        this.configurationWrapperField(object).forEach(field -> this.addSubscription(field, object));
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

        configListeners.forEach(configListener -> configListener.onChange(configEntityList));
    }

    @Override
    public List<Object> getFocusObjListByKey(String key) {
        return configSubscriberObjMap.get(key);
    }

    @Override
    public void refresh(ConfigRepository configRepository) {
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
            throw new SmartConfigApplicationException(ClassUtils.getFullFieldName(field) + " configKey is null or empty");
        }
        return configKey;
    }

    protected abstract String doParseKey(Field field);

    protected String resolveAnnotation(Annotation annotation) {
        return ClassUtils.resolveAnnotationKey(annotation);
    }

    public void propertyInject(Object bean) {
        this.configurationWrapperField(bean).stream()
                .filter(this::focus)
                // 过滤@Value；spring赋值
                .filter(field -> !field.isAnnotationPresent(Value.class))
                .forEach(field -> {
                    String configKey = parseKey(field);
                    ConfigEntity configEntity = Optional.ofNullable(configRepository.getConfigEntity(configKey))
                            .orElse(new ConfigEntity(configKey));
                    propertyInject(configEntity, Lists.newArrayList(field));
                });
    }

    /**
     * 获取对象的所有字段，可能是包装字段
     * 当当前对象是@Configuration类时，spring会增强该类，代理了该类；返回原始父类的字段
     *
     * @param bean 豆
     * @return {@link List}<{@link Field}>
     * @see org.springframework.context.annotation.ConfigurationClassEnhancer$EnhancedConfiguration
     * @see SpringConfigSubscription#configurationWrapperField(Object)
     */
    protected List<Field> configurationWrapperField(Object bean) {
        return Arrays.asList(bean.getClass().getDeclaredFields());
    }


    @Override
    public void propertyInject(ConfigEntity configEntity, List<Field> fieldList) {
        for (Field field : fieldList) {
            field.setAccessible(true);
            try {
                Object fieldValue = resolveValue(field, configEntity.getValue());
                List<Object> fieldTargetObjList = getFocusObjListByKey(configEntity.getKey());
                for (Object fieldTargetObj : fieldTargetObjList) {
                    if (fieldTargetObj == null) {
                        // 非spring应用，静态配置类
                        field.set(null, fieldValue);
                        continue;
                    }
                    // spring应用，有可能是代理对象，需要判断字段所在类是否为字段所在对象类型本身或者父类
                    if (field.getDeclaringClass().isAssignableFrom(fieldTargetObj.getClass())) {
                        field.set(fieldTargetObj, fieldValue);
                    }
                }
            } catch (Exception e) {
                throw new SmartConfigApplicationException(ClassUtils.getFullFieldName(field) + "resolveValue or inject error", e);
            }
        }
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
            throw new SmartConfigApplicationException(ClassUtils.getFullFieldName(field)
                    + " configValue cannot be null without a default value");
        }

        if (isSimpleTypeAnnotation(field)) {
            Class<?> fieldType = field.getType();

            if (String.class.isAssignableFrom(fieldType)) {
                return configValue;
            }

            if (Boolean.class.isAssignableFrom(fieldType) || boolean.class.isAssignableFrom(fieldType)) {
                return Boolean.parseBoolean(configValue);
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
            }

            if (JSON.isValid(configValue)) {
                throw new SmartConfigApplicationException("@Value or @SmartValue not support json,please use @JsonValue");
            }

            // 错误提示更加清晰，使用@Value，来进行json的
            throw new SmartConfigApplicationException("Unsupported type: " + fieldType.getName() + " for 【 "
                    + field.getDeclaringClass() + "." + field.getName() + " 】");
        }

        if (field.isAnnotationPresent(JsonValue.class)) {
            Type fieldGenericType = field.getGenericType();
            return JSON.parseObject(configValue, fieldGenericType);
        }

        throw new SmartConfigApplicationException("debug error");
    }
}
