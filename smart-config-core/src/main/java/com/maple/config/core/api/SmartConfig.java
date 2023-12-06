package com.maple.config.core.api;

import com.maple.config.core.model.ConfigEntity;

import java.util.Collection;
import java.util.List;

/**
 * @author yangfeng
 * @date : 2023/12/1 17:10
 * desc:
 */

public interface SmartConfig {


    void init(List<String> packagePathList, String localConfigPath);


    /**
     * 注册(变更)侦听器
     * 扫描指定路径的类，并注册value变更监听器
     * 会发生类加载行为
     *
     * @param classList 待注册字段观察者的类【也就是字段有注解修饰的类】
     */
    void registerListener(List<Class<?>> classList);

    void loadLocalFileConfig(String localFilePath);

    void initDefaultValue();


    Collection<ConfigEntity> configList();

    boolean containKey(String key);

    /**
     * 更改配置
     *
     * @param key   配置key
     * @param value 新value
     */
    void changeConfig(String key, String value);

}
