package com.maple.smart.config.core.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.maple.smart.config.core.model.ConfigEntity;
import java.util.Collection;

/**
 * JSON格式配置导出实现
 *
 * @author maple
 * @since 2025/06/27
 */
public class JsonConfigExporter implements ConfigExporter {
    private final ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    @Override
    public String export(Collection<ConfigEntity> configList) {
        try {
            return objectMapper.writeValueAsString(configList);
        } catch (Exception e) {
            throw new RuntimeException("导出配置为JSON失败", e);
        }
    }
}
