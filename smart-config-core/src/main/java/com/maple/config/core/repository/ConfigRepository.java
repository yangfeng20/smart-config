package com.maple.config.core.repository;

import com.maple.config.core.model.ConfigEntity;
import com.maple.config.core.subscription.ConfigSubscription;

import java.util.Collection;

/**
 * 配置仓库，加载，获取，解析，添加配置
 *
 * @author maple
 * @since 2024/3/7 21:18
 */

public interface ConfigRepository {

    /**
     * 添加配置
     *
     * @param configEntity config 实体
     * @return boolean
     */
    boolean addConfig(ConfigEntity configEntity);

    /**
     * 获取配置
     *
     * @param key key
     * @return {@link String}
     */
    String getConfig(String key);

    /**
     * 获取配置实体
     *
     * @param key key
     * @return {@link ConfigEntity}
     */
    ConfigEntity getConfigEntity(String key);

    /**
     * 配置列表
     *
     * @return {@link Collection}<{@link ConfigEntity}>
     */
    Collection<ConfigEntity> configList();

    /**
     * 已解析占位符的配置列表
     *
     * @return {@link Collection}<{@link ConfigEntity}>
     */
    Collection<ConfigEntity> resolvedPlaceholdersConfigList();

    /**
     * 解析占位符
     * 例如：${key--${innerKey}} --> value--innerValue
     *
     * @param text 带有占位符的文本
     * @return {@link String}
     */
    String resolvePlaceholders(String text);

    /**
     * 包含key
     *
     * @param key key
     * @return boolean
     */
    boolean containsKey(String key);

    /**
     * 发布配置
     * <p></p>
     * {@link AbsConfigRepository#waitReleaseKeyList} 中存在的key
     */
    void release();

    /**
     * 加载配置到仓库
     *
     * @param configEntityList 配置实体列表
     */
    void loader(Collection<ConfigEntity> configEntityList);

    /**
     * 设置订阅
     *
     * @param configSubscription 配置订阅者
     */
    void setSubscription(ConfigSubscription configSubscription);

    /**
     * 首次将配置更新到业务对象的字段中
     */
    void refresh();
}
