package com.maple.smart.config.core.export;

import com.maple.smart.config.core.model.ConfigEntity;
import java.util.Collection;

/**
 * 配置导出接口
 */
public interface ConfigExporter {
    /**
     * 导出配置
     * @param configList 配置集合
     * @return 导出内容字符串
     */
    String export(Collection<ConfigEntity> configList);
}
