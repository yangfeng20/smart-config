package com.maple.config.core.listener;

import com.alibaba.fastjson.JSON;
import com.maple.config.core.annotation.JsonValue;
import com.maple.config.core.annotation.SmartValue;
import com.maple.config.core.exp.SmartConfigApplicationException;
import com.maple.config.core.model.ConfigEntity;
import org.springframework.beans.factory.annotation.Value;

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
    @Override
    public Object getObjectByKey(String configKey) {
        return null;
    }

    protected Object getValue(Field field, String configValue) {
        if (field.isAnnotationPresent(SmartValue.class) || field.isAnnotationPresent(Value.class)) {
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
                Object fieldTargetObj = getObjectByKey(configEntity.getKey());
                field.set(fieldTargetObj, fieldValue);
            } catch (IllegalAccessException e) {
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
