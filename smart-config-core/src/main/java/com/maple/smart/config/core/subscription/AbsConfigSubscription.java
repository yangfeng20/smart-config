package com.maple.smart.config.core.subscription;

import com.maple.smart.config.core.annotation.JsonValue;
import com.maple.smart.config.core.annotation.SmartValue;
import com.maple.smart.config.core.exp.SmartConfigApplicationException;
import com.maple.smart.config.core.infrastructure.json.JSONFacade;
import com.maple.smart.config.core.inject.PropertyInject;
import com.maple.smart.config.core.listener.ConfigListener;
import com.maple.smart.config.core.model.ConfigEntity;
import com.maple.smart.config.core.model.ReleaseStatusEnum;
import com.maple.smart.config.core.repository.ConfigRepository;
import com.maple.smart.config.core.utils.ClassUtils;
import com.maple.smart.config.core.utils.Lists;
import com.maple.smart.config.core.utils.PlaceholderResolverUtils;
import com.maple.smart.config.core.utils.spring.SpringPropertyPlaceholderHelper;
import com.maple.smart.config.core.utils.spring.StringUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
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

@Slf4j
public abstract class AbsConfigSubscription implements ConfigSubscription, PropertyInject {

    @Setter
    @Getter
    protected ConfigRepository configRepository;

    protected List<ConfigListener> configListeners = new ArrayList<>();

    protected Map<String, List<Field>> configSubscriberMap = new HashMap<>(16);

    protected Map<String, List<Object>> configSubscriberObjMap = new HashMap<>(16);

    /**
     * 配置默认值回显
     */
    @Setter
    protected boolean defaultValEcho;

    protected List<ConfigEntity> defaultValEchoConfigList = new ArrayList<>();


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

        List<String> configKeyList = findAllKey(field);
        for (String configKey : configKeyList) {
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
    }

    @Override
    public void subscribe(List<ConfigEntity> configEntityList) {
        configEntityList.forEach(configEntity -> {
            List<Field> fieldList = configSubscriberMap.get(configEntity.getKey());
            if (fieldList == null || fieldList.isEmpty()) {
                // 当前配置没有字段观察者，下一个配置;没有观察者时，也更新发布状态
                configEntity.setStatus(ReleaseStatusEnum.RELEASE.getCode());
                return;
            }
            try {
                this.propertyInject(configEntity, fieldList);
                configEntity.setStatus(ReleaseStatusEnum.RELEASE.getCode());
            } catch (Exception e) {
                log.error("key [{}] has problems {}", configEntity.getKey(), e.getMessage(), e);
                throw new SmartConfigApplicationException("key [" + configEntity.getKey() + "] has problems " + e.getMessage());
            }
        });

        configListeners.forEach(configListener -> configListener.onChange(configEntityList));
    }

    @Override
    public List<Object> getFocusObjListByKey(String key) {
        return configSubscriberObjMap.get(key);
    }

    /**
     * 将配置仓库中所有的配置刷新到配置订阅者（字段）中
     * 调用时期，启动的最后阶段
     *
     * @param configRepository 配置存储库
     */
    @Override
    public void refresh(ConfigRepository configRepository) {
        Map<String, ConfigEntity> configEntityMap = configRepository.configList()
                .stream()
                .collect(Collectors.toMap(ConfigEntity::getKey, Function.identity()));
        for (Map.Entry<String, List<Field>> entry : configSubscriberMap.entrySet()) {
            String configKey = entry.getKey();
            List<Field> focusFieldList = entry.getValue();

            // 字段上的配置key在配置文件中找不到时，给出空配置实体【字段注解上可能有默认值】
            ConfigEntity configEntity = Optional.ofNullable(configEntityMap.get(configKey)).orElse(new ConfigEntity(configKey));
            this.propertyInject(configEntity, focusFieldList);
        }

        // 默认值回显
        if (!defaultValEcho && defaultValEchoConfigList.isEmpty()) {
            return;
        }
        configRepository.loader(defaultValEchoConfigList);
    }

    @Override
    public void addListener(ConfigListener configListener) {
        if (configListeners.contains(configListener)) {
            return;
        }
        configListeners.add(configListener);
    }

    /**
     * 当前字段是否应该被关注<p></p>
     * 影响到当前字段是否需要被添加到观察者列表；以及当前字段是否需要赋值注入
     *
     * @param field 字段
     * @return boolean
     * @see AbsConfigSubscription#addSubscription(Field, Object)
     * @see PropertyInject#propertyInject(Object)
     * @see AbsConfigSubscription#propertyInject(Object)
     */
    protected abstract boolean focus(Field field);

    /**
     * 找到字段注解上的所有key
     *
     * @param field 注解字段
     * @return keyList
     */
    private List<String> findAllKey(Field field) {
        String str = findFieldAnnotationValue(field);
        return PlaceholderResolverUtils.findAllKey(str);
    }

    protected abstract String findFieldAnnotationValue(Field field);

    @Override
    public void propertyInject(Object bean) {
        this.configurationWrapperField(bean).stream()
                .filter(this::focus)
                // 过滤@Value；spring赋值
                // todo @Value无法支持默认值回显到web控制台
                .filter(field -> !field.isAnnotationPresent(Value.class))
                .forEach(field -> {
                    String configKey = PlaceholderResolverUtils.findAnyKey(findFieldAnnotationValue(field));
                    ConfigEntity configEntity = Optional.ofNullable(configRepository.getOriginalConfigEntity(configKey))
                            .orElse(new ConfigEntity(configKey));
                    propertyInject(configEntity, Lists.newArrayList(field));
                });
    }

    /**
     * 获取对象的所有字段，可能是包装字段
     * 当当前对象是@Configuration类时，spring会增强该类，代理了该类；返回原始父类的字段
     *
     * @param bean bean
     * @return {@link List}<{@link Field}>
     * //* @see org.springframework.context.annotation.ConfigurationClassEnhancer$EnhancedConfiguration
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
                String annotationValue = findFieldAnnotationValue(field);
                Object fieldValue = resolveValue(field, annotationValue);
                List<Object> fieldTargetObjList = getFocusObjListByKey(configEntity.getKey());
                for (Object fieldTargetObj : fieldTargetObjList) {
                    if (fieldTargetObj == null) {
                        if (!Modifier.isStatic(field.getModifiers())) {
                            throw new SmartConfigApplicationException(ClassUtils.getFullFieldName(field)
                                    + " Non-springboot applications only support static fields");
                        }
                        // 非spring应用，静态配置类
                        field.set(null, fieldValue);
                        continue;
                    }
                    // spring应用，有可能是代理对象，需要判断字段所在类是否为字段所在对象类型本身或者父类
                    if (field.getDeclaringClass().isAssignableFrom(fieldTargetObj.getClass())) {
                        field.set(fieldTargetObj, fieldValue);
                    }
                    if (defaultValEcho && fieldValue != null && defaultValConfig(configEntity)) {
                        configEntity.setValue(annotationValue);
                        configEntity.setDesc("代码默认值回显");
                        defaultValEchoConfigList.add(configEntity);
                    }
                }
            } catch (Exception e) {
                throw new SmartConfigApplicationException(ClassUtils.getFullFieldName(field) + " resolveValue or inject error", e);
            }
        }
    }

    protected boolean isSimpleTypeAnnotation(Field field) {
        return field.isAnnotationPresent(SmartValue.class);
    }

    protected Object resolveValue(Field field, String annotationValue) {
        String fullFieldName = ClassUtils.getFullFieldName(field);

        // 解析默认值上可能存在的占位符
        SpringPropertyPlaceholderHelper placeholderHelper = new SpringPropertyPlaceholderHelper("${",
                "}", ":", false);
        String configValue = placeholderHelper.replacePlaceholders(annotationValue, configRepository::getConfig);

        // 配置文件中当前字段key没有对应值，且字段的直接上没有默认值，抛出异常
        if (configValue == null) {
            throw new SmartConfigApplicationException(fullFieldName
                    + " cannot be null without a default value");
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

            // 使用@Value 或者 @SmartValue 来进行json的value数据
            if (JSONFacade.isValid(configValue)) {
                throw new SmartConfigApplicationException(fullFieldName +
                        " @Value or @SmartValue not support json,please use @JsonValue");
            }

            // 字段是其他基本类型，未支持
            throw new SmartConfigApplicationException("Unsupported type: " + fieldType.getName() + " for 【 "
                    + field.getDeclaringClass() + "." + field.getName() + " 】");
        }

        if (field.isAnnotationPresent(JsonValue.class)) {
            Type fieldGenericType = field.getGenericType();
            // 字段是String类型，不支持;因为json解析之后可能少数据
            if (String.class.equals(fieldGenericType)) {
                throw new SmartConfigApplicationException(fullFieldName +
                        " @JsonValue not support String,please use @Value or @SmartValue");
            }

            // 不是有效的json格式，parseObject也能解析，但可能导致数据不准确
            if (!JSONFacade.isValid(configValue)) {
                log.warn("The configuration value associated with the [{}] field is not a valid json format," +
                        " and it is possible that the field is causing incorrect data", fullFieldName);
            }
            return JSONFacade.parseObject(configValue, fieldGenericType);
        }

        throw new SmartConfigApplicationException("The current field is not concerned, please refer to" +
                " [com.maple.config.core.subscription.AbsConfigSubscription.focus]");
    }


    /**
     * 是否为默认值配置
     * 即 配置实体只有key有值
     *
     * @param configEntity Config 实体
     * @return boolean
     */
    private boolean defaultValConfig(ConfigEntity configEntity) {
        if (configEntity == null || StringUtils.isEmpty(configEntity.getKey())) {
            return false;
        }

        return configEntity.getValue() == null && configEntity.getDesc() == null
                && configEntity.getStatus() == null
                && configEntity.getCreateDate() == null
                && configEntity.getUpdateDate() == null
                && !Boolean.TRUE.equals(configEntity.isDurable());
    }
}
