package com.maple.config.core.api.impl.local;

import com.maple.config.core.annotation.SmartValue;
import com.maple.config.core.api.AbsSmartConfig;
import com.maple.config.core.model.ConfigEntity;
import com.maple.config.core.utils.ClassScanner;
import com.maple.config.core.utils.TempConstant;
//import com.maple.config.core.web.ServerBootstrap;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author yangfeng
 * @since : 2023/12/1 17:16
 * desc:
 */

public class LocalFileConfig extends AbsSmartConfig {

    private int webUiPort;

    public LocalFileConfig(int webUiPort, boolean descInfer) {
        super(descInfer);
        this.webUiPort = webUiPort;
    }
    public LocalFileConfig(boolean descInfer) {
        super(descInfer);
    }

    @Override
    protected boolean isRegister(Field field) {
        // 因为在本地配置中，所有的实例对象都是手动创建，没有容器管理，在更新字段时无法获取到实例对象，所以仅支持静态字段
        if (!Modifier.isStatic(field.getModifiers())) {
            return false;
        }
        if (!field.isAnnotationPresent(SmartValue.class)) {
            return false;
        }

        SmartValue annotation = field.getAnnotation(SmartValue.class);
        String value = annotation.value();
        return value != null && !value.isEmpty();
    }

    @Override
    protected String getKey(Field field) {
        SmartValue annotation = field.getAnnotation(SmartValue.class);
        if (annotation == null) {
            return null;
        }
        String[] split = annotation.value().split(":", 1);
        if (split.length == 0) {
            return annotation.value();
        }
        return split[0];
    }

    @Override
    protected String propertyInject(Field field, String value) {
        SmartValue annotation = field.getAnnotation(SmartValue.class);
        if (annotation == null) {
            return null;
        }

        String annotationVal = annotation.value();

        String newValue = value;
        String[] split = annotationVal.split(":");
        // 本地文件无值并且字段上有默认值
        if (value == null && split.length == 2) {
            // 优先获取字段注解上的值
            newValue = split[1];
        }

        field.setAccessible(true);
        try {
            field.set(null, newValue);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return newValue;
    }

    @Override
    protected void customInit() {
        List<String> packagePathList = TempConstant.packagePathList;
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


        this.registerListener(scannerResult);
        this.initDefaultValue();
        this.startWebUi();
    }

    private void startWebUi() {
        if (webUiPort == 0){
            webUiPort = 6767;
        }
        new Thread(() -> {
            try {
                //new ServerBootstrap(this).start(webUiPort);
            } catch (Exception e) {
                throw new RuntimeException("Smart-config:启动webUi失败", e);
            }
        }).start();
    }

    public void initDefaultValue() {
        for (Map.Entry<String, List<Field>> entry : configObserverMap.entrySet()) {
            ConfigEntity configEntity = configEntityMap.get(entry.getKey());
            // value = null时当前配置仅存在字段上，未在本地配置文件中配置
            String localFileValue = null;
            if (configEntity != null) {
                localFileValue = configEntity.getValue();
            }

            // 本地文件和字段注解上都有这个key，优先取配置文件上的值【字段上只是默认值】
            for (Field field : entry.getValue()) {
                String newValue = propertyInject(field, localFileValue);
                if (configEntity == null || configEntity.getValue().equals(newValue)) {
                    continue;
                }
                // 更新配置值
                configEntity.setValue(newValue);
                configEntity.setUpdateDate(new Date());

            }
        }
    }

    /**
     * 注册(变更)侦听器
     * 扫描指定路径的类，并注册value变更监听器
     * 会发生类加载行为
     *
     * @param classList 待注册字段观察者的类【也就是字段有注解修饰的类】
     */
    public void registerListener(List<Class<?>> classList) {
        for (Class<?> clazz : classList) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                // 字段是否满足注册观察者条件
                if (!isRegister(field)) {
                    continue;
                }

                // 获取字段注解上的key
                String configKey = getKey(field);
                if (configKey == null) {
                    continue;
                }

                // 当前配置key关联的所有观察者
                List<Field> keyLinkFieldList = configObserverMap.getOrDefault(configKey, new ArrayList<>());
                keyLinkFieldList.add(field);
                configObserverMap.put(configKey, keyLinkFieldList);
            }
        }
    }

    @Override
    public Object getObjectByKey(String key) {
        return null;
    }
}
