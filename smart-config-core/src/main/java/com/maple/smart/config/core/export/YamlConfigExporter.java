package com.maple.smart.config.core.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.maple.smart.config.core.model.ConfigEntity;
import java.util.Collection;

/**
 * YAML格式配置导出实现
 *
 * @author maple
 * @since 2025/06/30
 */
public class YamlConfigExporter implements ConfigExporter {
    private final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

    @Override
    public String export(Collection<ConfigEntity> configList) {
        try {
            return objectMapper.writeValueAsString(configList);
        } catch (Exception e) {
            throw new RuntimeException("导出配置为YAML失败", e);
        }
    }
}
