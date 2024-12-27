package com.maple.smart.config.core.infrastructure.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;


@AllArgsConstructor
public class JsonObject {

    private ObjectNode objectNode;

    private final ObjectMapper objectMapper;

    // put 方法接受任何类型的对象，自动处理不同类型的值
    public void put(String key, Object value) {
        if (value == null) {
            // 如果 value 为 null，可以选择直接设置为 null，或使用 "null" 字符串等
            objectNode.putNull(key);
        } else if (value instanceof String) {
            objectNode.put(key, (String) value);
        } else if (value instanceof Integer) {
            objectNode.put(key, (Integer) value);
        } else if (value instanceof Boolean) {
            objectNode.put(key, (Boolean) value);
        } else if (value instanceof Double) {
            objectNode.put(key, (Double) value);
        } else if (value instanceof Long) {
            objectNode.put(key, (Long) value);
        } else if (value instanceof JsonNode) {
            // 如果是 JsonNode 类型（例如嵌套的 JsonObject）
            objectNode.set(key, (JsonNode) value);
        } else {
            // 如果是其他类型，尝试将其转换为 JsonNode
            JsonNode jsonNode = objectMapper.valueToTree(value);
            objectNode.set(key, jsonNode);
        }
    }

    public String toJsonStr() {
        try {
            // 使用 ObjectMapper 将 Java 对象转换为 JSON 字符串
            return objectMapper.writeValueAsString(objectNode);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
