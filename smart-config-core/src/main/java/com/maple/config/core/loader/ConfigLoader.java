package com.maple.config.core.loader;

import com.maple.config.core.model.ConfigEntity;

import java.util.Collection;

/**
 * 配置加载器
 *
 * @author maple
 * @since 2024/3/7 21:28
 */

public interface ConfigLoader {

    Collection<ConfigEntity> loaderConfig(String path);

    /**
     * 是否开启配置描述解析
     *
     * @param openConfigInferDesc 是否开启配置描述推断
     */
    void setConfigInferDesc(boolean openConfigInferDesc);
}
