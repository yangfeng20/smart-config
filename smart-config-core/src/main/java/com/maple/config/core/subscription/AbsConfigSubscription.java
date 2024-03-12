package com.maple.config.core.subscription;

import com.maple.config.core.listener.ConfigListener;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * @author maple
 * Created Date: 2024/3/7 21:50
 * Description:
 */

public abstract class AbsConfigSubscription implements ConfigSubscription{

    protected Map<String, List<Field>> configSubscriberMap;


    protected List<ConfigListener> configListeners;

}
