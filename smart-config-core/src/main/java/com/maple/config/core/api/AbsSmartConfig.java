package com.maple.config.core.api;

import com.maple.config.core.utils.ClassScanner;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yangfeng
 * @date : 2023/12/1 17:15
 * desc:
 */

public abstract class AbsSmartConfig implements SmartConfig {

    protected Map<String, List<Field>> configObserverMap = new HashMap<>();


    @Override
    public Object configList() {
        return null;
    }

    @Override
    public void changeConfig(String key, String value) {
        if (!configObserverMap.containsKey(key)) {
            return;
        }

        for (Field field : configObserverMap.get(key)) {
            field.setAccessible(true);
            try {
                // todo 没有实例对象，仅支持静态字段
                field.set(null, value);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

    }

    @Override
    public void login(Object loginParam) {

    }

    @Override
    public void registerListener(List<String> packagePathList) {
        if (packagePathList == null || packagePathList.isEmpty()) {
            throw new IllegalArgumentException("请指定包名路径");
        }

        List<Class<?>> scannerResult = new ArrayList<>();
        for (String packagePath : packagePathList) {
            try {
                List<Class<?>> classes = ClassScanner.getClasses(packagePath);
                scannerResult.addAll(classes);
            } catch (ClassNotFoundException | IOException e) {
                throw new RuntimeException(e);
            }
        }

        for (Class<?> clazz : scannerResult) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if (!isRegister(field)) {
                    continue;
                }

                String configKey = getKey(field);
                if (configKey == null){
                    continue;
                }
                setDefaultVal(field);

                List<Field> keyLinkFieldList = configObserverMap.getOrDefault(configKey, new ArrayList<>());
                keyLinkFieldList.add(field);
                configObserverMap.put(configKey, keyLinkFieldList);
            }
        }
    }


    abstract boolean isRegister(Field field);


    abstract String getKey(Field field);

    abstract void setDefaultVal(Field field);
}
