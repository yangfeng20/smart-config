package com.maple.config.core.boot;

import com.maple.config.core.repository.ConfigRepository;


/**
 * 智能配置引导接口。该接口定义了初始化、刷新配置以及获取配置仓库的方法，用于实现动态配置更新和管理。
 *
 * @author maple
 * @since 2024/3/13 22:28
 */
public interface SmartConfigBootstrap {

    /**
     * 初始化方法<p></p>用于执行配置的初始化操作，加载spi实现以及加载配置。
     */
    void init();

    /**
     * 刷新配置方法。将仓库配置在初始化阶段刷新到字段上，更新字段的值。如果未启动webUI，则启动webUI
     */
    void refreshConfig();

    /**
     * 获取配置仓库方法。该方法用于获取当前配置管理实例所使用的配置仓库实例对象。
     *
     * @return {@link ConfigRepository} 返回当前使用的配置仓库实例。
     */
    ConfigRepository getConfigRepository();
}

