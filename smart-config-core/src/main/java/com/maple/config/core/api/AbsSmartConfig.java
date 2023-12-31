package com.maple.config.core.api;

import com.maple.config.core.exp.SmartConfigApplicationException;
import com.maple.config.core.model.ConfigEntity;
import com.maple.config.core.model.ReleaseStatusEnum;
import com.maple.config.core.utils.TempConstant;

import java.lang.reflect.Field;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author yangfeng
 * @since : 2023/12/1 17:15
 * desc:
 */

public abstract class AbsSmartConfig implements SmartConfig {

    protected Map<String, ConfigEntity> configEntityMap = new HashMap<>();

    protected Map<String, List<Field>> configObserverMap = new HashMap<>();

    protected Collection<String> waitReleaseConfigKeyList = new CopyOnWriteArrayList<>();

    // 配置描述推断
    private final boolean descInfer;

    private final Map<String, Object> extMap = new HashMap<>();

    public AbsSmartConfig(boolean descInfer) {
        this.descInfer = descInfer;
    }

    @Override
    public void init(List<String> packagePathList, String localConfigPath) {
        TempConstant.packagePathList = packagePathList;
        TempConstant.localConfigPath = localConfigPath;
        // 加载本地配置文件
        this.loadLocalFileConfig(localConfigPath);

        // 子类自定义初始化操作
        customInit();
    }

    /**
     * 自定义初始化
     */
    protected abstract void customInit();

    public void loadLocalFileConfig(String localConfigPath) {
        List<String> lineDataList;
        try {
            URL resource = this.getClass().getResource("/");
            if (resource == null) {
                throw new RuntimeException("获取路径url为空");
            }
            Path basePath = Paths.get(resource.toURI());
            Path filePath = basePath.resolve(localConfigPath);
            lineDataList = Files.readAllLines(filePath, Charset.defaultCharset());

        } catch (Exception e) {
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
            // 过滤注释的配置
            if (!descInfer && lineStr.startsWith("#")){
                continue;
            }
            // 过滤空行和文件不规范的数据
            if (!lineStr.contains("=")) {
                desc = "";
                continue;
            }

            String[] strArr = lineStr.split("=");
            String key = strArr[0].trim();
            String value;
            // 配置文件不规范，只有=号，没有后面的值
            if (strArr.length < 2) {
                value = "";
            } else {
                value = strArr[1].trim();
            }

            buildAuthInfo(key, value);

            ConfigEntity configEntity = new ConfigEntity(key, value, ReleaseStatusEnum.RELEASE.getCode());
            configEntity.setDesc(desc);
            configEntity.setCreateDate(createDate);
            configEntity.setDurable(true);
            configEntityList.add(configEntity);
        }
        configEntityMap = configEntityList.stream()
                .collect(Collectors.toMap(ConfigEntity::getKey, Function.identity()));
        checkAuthInfo();
    }

    private void checkAuthInfo() {
        if (getExtMap().containsKey("username") && getExtMap().containsKey("password")) {
            return;
        }

        throw new SmartConfigApplicationException("未在配置文件中配置webUi用户名和密码;[smart.username=xxx;smart.password=xxx;]");
    }

    private void buildAuthInfo(String key, String value) {
        if ("smart.username".equals(key)) {
            getExtMap().put("username", value);
        }
        if ("smart.password".equals(key)) {
            getExtMap().put("password", value);
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
            // 过滤瞬时配置在页面更新之后的key【已页面为最新配置】
            if (waitReleaseConfigKeyList.contains(key)) {
                continue;
            }
            Field field = configObserverMap.get(key).get(0);
            field.setAccessible(true);
            Object keyLinkObject = getObjectByKey(key);
            StringBuilder value = new StringBuilder();
            try {

                if (keyLinkObject instanceof List) {
                    List<?> list = (List<?>) keyLinkObject;
                    // spring项目同一个key多个不同的默认值
                    for (Object obj : list) {
                        value.append(field.get(obj)).append(" | ");
                    }
                } else {
                    value.append(field.get(keyLinkObject));
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            instantConfigList.add(new ConfigEntity(key, value.toString(), ReleaseStatusEnum.RELEASE.getCode()));
        }

        ArrayList<ConfigEntity> result = new ArrayList<>(configEntityMap.values());
        result.addAll(instantConfigList);
        return result;
    }

    public abstract Object getObjectByKey(String key);

    @Override
    public void release(Collection<String> keyList) {
        if (keyList == null || keyList.isEmpty()) {
            keyList = waitReleaseConfigKeyList;
        }

        for (String key : keyList) {
            List<Field> fieldList = configObserverMap.get(key);
            if (fieldList == null || fieldList.isEmpty()) {
                // 当前配置还未添加到字段上，没有对应的观察者
                continue;
            }
            for (Field field : fieldList) {
                field.setAccessible(true);
                try {
                    ConfigEntity configEntity = configEntityMap.get(key);
                    Object keyLinkObj = getObjectByKey(key);
                    if (keyLinkObj instanceof List) {
                        for (Object obj : ((List<?>) keyLinkObj)) {
                            field.set(obj, configEntity.getValue());
                        }
                    } else {
                        field.set(keyLinkObj, configEntity.getValue());
                    }
                    configEntity.setStatus(ReleaseStatusEnum.RELEASE.getCode());
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
            keyList.remove(key);
        }
    }

    @Override
    public void addConfig(String key, String value) {
        if (this.containKey(key)) {
            throw new SmartConfigApplicationException("配置key已存在，无法添加");
        }

        ConfigEntity configEntity = new ConfigEntity(key, value, ReleaseStatusEnum.NOT_RELEASE.getCode());
        configEntity.setCreateDate(new Date());
        configEntityMap.put(key, configEntity);
        waitReleaseConfigKeyList.add(key);
    }

    @Override
    public void changeConfig(String key, String value) {
        if (!this.containKey(key)) {
            return;
        }
        waitReleaseConfigKeyList.add(key);

        if (configEntityMap.containsKey(key)) {
            ConfigEntity configEntity = configEntityMap.get(key);
            configEntity.setValue(value);
            configEntity.setUpdateDate(new Date());
            configEntity.setStatus(ReleaseStatusEnum.NOT_RELEASE.getCode());

            return;
        }

        // 以下仅为瞬时配置
        ConfigEntity instantConfigEntity = new ConfigEntity(key, value, ReleaseStatusEnum.NOT_RELEASE.getCode());
        instantConfigEntity.setUpdateDate(new Date());
        configEntityMap.put(key, instantConfigEntity);
    }

    @Override
    public Map<String, Object> getExtMap() {
        return extMap;
    }

    /**
     * 当前字段是否注册观察者
     *
     * @param field field
     * @return boolean
     */
    protected abstract boolean isRegister(Field field);


    protected abstract String getKey(Field field);

    protected abstract String propertyInject(Field field, String value);

    @Override
    public boolean containKey(String key) {
        return configEntityMap.containsKey(key) || configObserverMap.containsKey(key);
    }
}
