package com.maple.config.core.listener;

import com.maple.config.core.model.ConfigEntity;
import com.maple.config.core.subscription.ConfigSubscription;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

/**
 * @author maple
 * Created Date: 2024/3/7 21:55
 * Description:
 */

public interface ConfigListener {

    void propertyInject(ConfigEntity configEntity, List<Field> fieldList);


    void onChange(Collection<ConfigEntity> changeConfigEntityList);

    List<Object> getObjectListByKey(String configKey);

    void setConfigSubscription(ConfigSubscription configSubscription);
}
