package com.maple.smart.config.core.infrastructure.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;

/**
 * JSON facade
 *
 * @author maple
 * @since 2024/12/26
 */

public class JSONFacade {

    // 创建 ObjectMapper 实例（可以复用）
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

    /**
     * 将 Java 对象转换为 JSON 字符串
     *
     * @param javaObject Java 对象
     * @return JSON 字符串
     */
    public static String toJsonStr(Object javaObject) {
        try {
            // 使用 ObjectMapper 将 Java 对象转换为 JSON 字符串
            return OBJECT_MAPPER.writeValueAsString(javaObject);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将 JSON 字符串解析为指定类型的 Java 对象（基于 Class）
     *
     * @param str         JSON 字符串
     * @param objectClass Java 对象的 Class
     * @param <T>         Java 对象的类型
     * @return Java 对象
     */
    public static <T> T parseObject(String str, Class<T> objectClass) {
        try {
            // 使用 ObjectMapper 将 JSON 字符串反序列化为指定类型的 Java 对象
            return OBJECT_MAPPER.readValue(str, objectClass);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将 JSON 字符串解析为指定类型的 Java 对象（基于 Type）
     *
     * @param str        JSON 字符串
     * @param objectType Java 对象的 Type
     * @param <T>        Java 对象的类型
     * @return Java 对象
     */
    public static <T> T parseObject(String str, Type objectType) {
        try {
            // 使用 ObjectMapper 将 JSON 字符串反序列化为指定类型的 Java 对象
            return OBJECT_MAPPER.readValue(str, OBJECT_MAPPER.getTypeFactory().constructType(objectType));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isValid(String str) {
        try {
            // 尝试将字符串解析为 JsonNode（JSON 树的根节点）
            OBJECT_MAPPER.readTree(str);
            // 解析成功，说明是有效的 JSON
            return true;
        } catch (JsonProcessingException e) {
            // 如果抛出 JsonProcessingException，说明不是有效的 JSON
            return false;
        }
    }

    public static JsonObject createJsonObject() {
        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        return new JsonObject(objectNode, OBJECT_MAPPER);
    }
}
