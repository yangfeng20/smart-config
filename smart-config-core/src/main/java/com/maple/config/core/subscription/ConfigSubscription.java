package com.maple.config.core.subscription;

import com.maple.config.core.listener.ConfigListener;

import java.lang.reflect.Field;

/**
 * @author maple
 * Created Date: 2024/3/7 21:41
 * Description:
 */

public interface ConfigSubscription {


    // key 在field上，是否需要订阅过滤,赋值是否放在这里做
    void addSubscription(Field field);

    void addListener(ConfigListener configListener);

    void refresh();
}
