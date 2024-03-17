package com.maple.config.core.subscription;

import com.maple.config.core.listener.ConfigListener;
import com.maple.config.core.model.ConfigEntity;
import com.maple.config.core.repository.ConfigRepository;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author maple
 * Created Date: 2024/3/7 21:41
 * Description:
 */

public interface ConfigSubscription {


    // key 在field上，是否需要订阅过滤,赋值是否放在这里做
    void addSubscription(Field field, Object targetObj);

    void addSubscription(Object targetObj);

    void addSubscription(Class<?> clazz);

    void addListener(ConfigListener configListener);

    void refresh(ConfigRepository configRepository);

    void subscribe(List<ConfigEntity> configEntityList);


    List<Object> getFocusObjListByKey(String key);

    ConfigRepository getConfigRepository();
}
