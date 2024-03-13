package com.maple.config.core.subscription;

import com.maple.config.core.listener.ConfigListener;
import com.maple.config.core.model.ConfigEntity;
import com.maple.config.core.repository.ConfigRepository;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author maple
 * Created Date: 2024/3/7 21:50
 * Description:
 */

public abstract class AbsConfigSubscription implements ConfigSubscription {

    protected Map<String, List<Field>> configSubscriberMap;


    protected List<ConfigListener> configListeners = new ArrayList<>();

    @Override
    public void addSubscription(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            this.addSubscription(field);
        }
    }

    @Override
    public void addSubscription(Field field) {
        if (!focus(field)) {
            return;
        }

        String configKey = parseKey(field);
        if (configSubscriberMap.containsKey(configKey)) {
            configSubscriberMap.get(configKey).add(field);
        } else {
            List<Field> fieldList = new ArrayList<>();
            fieldList.add(field);
            configSubscriberMap.put(configKey, fieldList);
        }
    }

    @Override
    public void subscribe(List<ConfigEntity> configEntityList) {
        configListeners.forEach(configListener -> {
            configListener.onChange(configEntityList);
        });
    }

    @Override
    public void refresh(ConfigRepository configRepository) {
        Map<String, ConfigEntity> configEntityMap = configRepository.configList().stream().collect(Collectors.toMap(ConfigEntity::getKey, Function.identity()));
        for (Map.Entry<String, List<Field>> entry : configSubscriberMap.entrySet()) {
            String configKey = entry.getKey();
            List<Field> focusFieldList = entry.getValue();
            ConfigEntity configEntity = configEntityMap.get(configKey);

            configListeners.forEach(configListener -> {
                configListener.propertyInject(configEntity, focusFieldList);
            });
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

    protected abstract String parseKey(Field field);
}
