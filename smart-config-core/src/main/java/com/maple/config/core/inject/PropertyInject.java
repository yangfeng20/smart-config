package com.maple.config.core.inject;

import com.maple.config.core.model.ConfigEntity;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author maple
 * Created Date: 2024/3/17 10:27
 * Description:
 */

public interface PropertyInject {

    void propertyInject(ConfigEntity configEntity, List<Field> fieldList);
}
