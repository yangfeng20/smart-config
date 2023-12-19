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


    /**
     * 初始化配置
     * 内部包含加载本地文件配置
     * {@see com.maple.config.core.api.SmartConfig#loadLocalFileConfig(java.lang.String)}
     * 注册字段观察者
     * {@see com.maple.config.core.api.SmartConfig#registerListener(java.util.List)}
     * 配置属性注入
     * {@see com.maple.config.core.api.SmartConfig#initDefaultValue()}
     *
     * @param packagePathList 包路径列表
     * @param localConfigPath 本地配置路径
     */
    void init(List<String> packagePathList, String localConfigPath);

    /**
     * 加载本地文件配置到内存中
     *
     * @param localFilePath 本地文件路径
     */
    void loadLocalFileConfig(String localFilePath);


    /**
     * 获取配置列表
     * 包含持久化配置和瞬时配置
     * 在未修改瞬时配置的前提下，瞬时配置优先
     *
     * @return {@link Collection}<{@link ConfigEntity}>
     */
    Collection<ConfigEntity> configList();

    /**
     * 是否包含之前的配置key
     * 包含持久化配置和瞬时配置
     *
     * @param key key
     * @return boolean
     */
    boolean containKey(String key);

    /**
     * 更改配置
     *
     * @param key   配置key
     * @param value 新value
     */
    void changeConfig(String key, String value);

    /**
     * 添加配置
     * 添加的配置为瞬时配置，未持久化
     *
     * @param key   钥匙
     * @param value 价值
     */
    void addConfig(String key, String value);

    /**
     * 发布配置
     * 当为null或者空列表时，发布所有待发布的key
     *
     * @param keyList key列表 仅发布指定的key
     */
    void release(Collection<String> keyList);

}
