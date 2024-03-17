package com.maple.config.core.boot;

import com.maple.config.core.repository.ConfigRepository;


/**
 * 智能配置引导接口。该接口定义了初始化、刷新配置以及获取配置仓库的方法，用于实现动态配置更新和管理。
 *
 * @author maple
 * Created Date: 2024/3/13 22:28
 */
public interface SmartConfigBootstrap {

    /**
     * 初始化方法。用于执行配置的初始化操作，加载spi实现以及加载配置。
     */
    void init();

    /**
     * 刷新配置方法。用于从配置源更新配置信息，实现配置的动态刷新。
     */
    void refreshConfig();

    /**
     * 获取配置仓库方法。该方法用于获取当前配置管理实例所使用的配置仓库。
     *
     * @return {@link ConfigRepository} 返回当前使用的配置仓库实例。
     */
    ConfigRepository getConfigRepository();
}

