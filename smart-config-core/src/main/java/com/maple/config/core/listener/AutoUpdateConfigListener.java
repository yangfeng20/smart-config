package com.maple.config.core.listener;

import com.alibaba.fastjson.JSON;
import com.maple.config.core.annotation.JsonValue;
import com.maple.config.core.annotation.SmartValue;
import com.maple.config.core.exp.SmartConfigApplicationException;
import com.maple.config.core.model.ConfigEntity;
import com.maple.config.core.subscription.ConfigSubscription;
import com.maple.config.core.utils.ClassUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.List;

/**
 * @author maple
 * Created Date: 2024/3/7 22:46
 * Description:
 */

public class AutoUpdateConfigListener implements ConfigListener {

    private ConfigSubscription configSubscription;

    @Override
    public List<Object> getObjectListByKey(String configKey) {
        return configSubscription.getFocusObjListByKey(configKey);
    }

    @Override
    public void setConfigSubscription(ConfigSubscription configSubscription) {
        this.configSubscription = configSubscription;
    }

    protected boolean isSimpleTypeAnnotation(Field field) {
        return field.isAnnotationPresent(SmartValue.class);
    }

    protected Object getValue(Field field, String configValue) {
        // 配置文件中当前字段key没有对应值，查看字段的直接上是否有默认值
        if (configValue == null) {
            configValue = ClassUtils.resolveAnnotation(field.getAnnotation(SmartValue.class)).getValue();
            if (configValue == null) {
                configValue = ClassUtils.resolveAnnotation(field.getAnnotation(JsonValue.class)).getValue();
            }
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

    @Override
    public void propertyInject(ConfigEntity configEntity, List<Field> fieldList) {
        fieldList.forEach(field -> {
            field.setAccessible(true);
            try {
                Object fieldValue = getValue(field, configEntity.getValue());
                List<Object> fieldTargetObjList = getObjectListByKey(configEntity.getKey());
                for (Object fieldTargetObj : fieldTargetObjList) {
                    field.set(fieldTargetObj, fieldValue);
                }
            } catch (IllegalAccessException e) {
                // todo 日志
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onChange(Collection<ConfigEntity> changeConfigEntityList) {
        for (ConfigEntity configEntity : changeConfigEntityList) {

        }

    }
}
