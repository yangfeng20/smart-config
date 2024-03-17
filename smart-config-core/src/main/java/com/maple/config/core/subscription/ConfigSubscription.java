package com.maple.config.core.subscription;

import com.maple.config.core.listener.ConfigListener;
import com.maple.config.core.model.ConfigEntity;
import com.maple.config.core.repository.ConfigRepository;

import java.util.List;

/**
 * 配置订阅者
 * 用于处理配置发布之后的更新等
 *
 * @author maple
 * @since 2024/3/7 21:41
 * Description:
 */

public interface ConfigSubscription {

    /**
     * 添加订阅
     *
     * @param targetObj 目标Obj
     */
    void addSubscription(Object targetObj);

    /**
     * 添加订阅
     *
     * @param clazz class
     */
    void addSubscription(Class<?> clazz);

    /**
     * 添加监听者
     *
     * @param configListener 配置监听者
     */
    void addListener(ConfigListener configListener);

    /**
     * 刷新
     *
     * @param configRepository 配置存储库
     */
    void refresh(ConfigRepository configRepository);

    /**
     * 订阅
     *
     * @param configEntityList 配置实体列表
     */
    void subscribe(List<ConfigEntity> configEntityList);

    /**
     * 按key获取关注的obj列表
     *
     * @param key 钥匙
     * @return {@link List}<{@link Object}>
     */
    List<Object> getFocusObjListByKey(String key);

    /**
     * 获取配置存储库
     *
     * @return {@link ConfigRepository}
     */
    ConfigRepository getConfigRepository();
}
