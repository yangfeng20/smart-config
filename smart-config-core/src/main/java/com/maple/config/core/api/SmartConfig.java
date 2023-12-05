package com.maple.config.core.api;

import com.maple.config.core.model.ConfigEntity;

import java.util.List;

/**
 * @author yangfeng
 * @date : 2023/12/1 17:10
 * desc:
 */

public interface SmartConfig {


    /**
     * 注册(变更)侦听器
     * 扫描指定路径的类，并注册value变更监听器
     * 会发生类加载行为
     *
     * @param packagePathList 包路径列表
     */
    void registerListener(List<String> packagePathList);


    List<ConfigEntity> configList();

    boolean containKey(String key);

    /**
     * 更改配置
     *
     * @param key   配置key
     * @param value 新value
     */
    void changeConfig(String key, String value);

    void login(Object loginParam);


}
