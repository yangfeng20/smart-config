package com.maple.config.core.inject;

import com.maple.config.core.model.ConfigEntity;

import java.lang.reflect.Field;
import java.util.List;

/**
 * 提供字段注入能力
 * 将configEntity关联的字段值注入到对象中
 *
 * @author maple
 * @since 2024/3/17 10:27
 */

public interface PropertyInject {

    void propertyInject(ConfigEntity configEntity, List<Field> fieldList);

    void propertyInject(Object bean);
}
