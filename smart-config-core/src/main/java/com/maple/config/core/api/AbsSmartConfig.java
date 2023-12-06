package com.maple.config.core.api;

import com.maple.config.core.model.ConfigEntity;
import com.maple.config.core.model.ReleaseStatusEnum;
import com.maple.config.core.utils.ClassScanner;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author yangfeng
 * @date : 2023/12/1 17:15
 * desc:
 */

public abstract class AbsSmartConfig implements SmartConfig {

    protected Map<String, ConfigEntity> configEntityMap = new HashMap<>();

    protected Map<String, List<Field>> configObserverMap = new HashMap<>();

    // 配置描述推断
    private final boolean descInfer;

    public AbsSmartConfig(boolean descInfer) {
        this.descInfer = descInfer;
    }

    @Override
    public void init(List<String> packagePathList, String localConfigPath) {
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

        this.loadLocalFileConfig(localConfigPath);
        this.registerListener(scannerResult);
        this.initDefaultValue();
    }

    public void loadLocalFileConfig(String localConfigPath) {
        List<String> lineDataList;
        try {
            // todo 路径适配问题
            String basePath = URLDecoder.decode(this.getClass().getResource("/").getPath(), "utf-8").substring(1);
            lineDataList = Files.readAllLines(Paths.get(basePath + localConfigPath), Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException("加载本地配置文件失败", e);
        }


        String desc = "";
        Date createDate = new Date();
        List<ConfigEntity> configEntityList = new ArrayList<>((int) (lineDataList.size() / 0.75) + 1);
        for (String lineStr : lineDataList) {
            if (descInfer && lineStr.startsWith("#")) {
                desc = lineStr.substring(1).trim();
                continue;
            }
            // 过滤空行和文件不规范的数据
            if (!lineStr.contains("=")) {
                continue;
            }

            String[] strArr = lineStr.split("=");
            String key = strArr[0].trim();
            String value = strArr[1].trim();

            ConfigEntity configEntity = new ConfigEntity(key, value, ReleaseStatusEnum.RELEASE.getCode());
            configEntity.setDesc(desc);
            configEntity.setCreateDate(createDate);
            configEntity.setDurable(true);
            configEntityList.add(configEntity);
        }
        configEntityMap = configEntityList.stream()
                .collect(Collectors.toMap(ConfigEntity::getKey, Function.identity()));
    }


    @Override
    public void registerListener(List<Class<?>> classList) {
        for (Class<?> clazz : classList) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if (!isRegister(field)) {
                    continue;
                }

                String configKey = getKey(field);
                if (configKey == null) {
                    continue;
                }

                List<Field> keyLinkFieldList = configObserverMap.getOrDefault(configKey, new ArrayList<>());
                keyLinkFieldList.add(field);
                configObserverMap.put(configKey, keyLinkFieldList);
            }
        }
    }

    @Override
    public void initDefaultValue() {
        for (Map.Entry<String, List<Field>> entry : configObserverMap.entrySet()) {
            ConfigEntity configEntity = configEntityMap.get(entry.getKey());
            // value = null时当前配置仅存在字段上，未在本地配置文件中配置
            String localFileValue = null;
            if (configEntity != null) {
                localFileValue = configEntity.getValue();
            }

            // 本地文件和字段注解上都有这个key，优先取字段注解上的值
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


    @Override
    public Collection<ConfigEntity> configList() {

        // configEntityMap - configObserverMap 的差集 【仅在字段注解上存在的key】
        Set<String> difference = new HashSet<>(configObserverMap.keySet());
        difference.removeAll(configEntityMap.keySet());

        // 获取所有的瞬时配置
        List<ConfigEntity> instantConfigList = new ArrayList<>();
        for (String key : difference) {
            Field field = configObserverMap.get(key).get(0);
            field.setAccessible(true);
            String value;
            try {
                value = (String) field.get(null);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            instantConfigList.add(new ConfigEntity(key, value, ReleaseStatusEnum.RELEASE.getCode()));
        }

        ArrayList<ConfigEntity> result = new ArrayList<>(configEntityMap.values());
        result.addAll(instantConfigList);
        return result;
    }

    @Override
    public void changeConfig(String key, String value) {
        if (!this.containKey(key)) {
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

        ConfigEntity configEntity = configEntityMap.get(key);
        if (configEntity == null) {
            return;
        }
        configEntity.setValue(value);
        configEntity.setUpdateDate(new Date());

    }


    abstract boolean isRegister(Field field);


    abstract String getKey(Field field);

    abstract String propertyInject(Field field, String value);

    abstract Class<? extends Annotation> getFieldAnnotation();

    @Override
    public boolean containKey(String key) {
        return configEntityMap.containsKey(key) || configObserverMap.containsKey(key);
    }
}
