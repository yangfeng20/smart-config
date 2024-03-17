package com.maple.smart.config.test;

import com.maple.config.core.annotation.JsonValue;
import com.maple.config.core.annotation.SmartValue;
import com.maple.config.core.model.ConfigEntity;

import java.util.List;

/**
 * @author yangfeng
 * @since : 2023/12/4 9:54
 * desc:
 */

public class Service {

    @SmartValue("${aaa:111}")
    private static String val;
    @SmartValue("${bbb:字段值}")
    private static String val1;

    @SmartValue("${ddd:前缀-${ccc}}")
    private static String val2;

    @SmartValue("${ddd:前缀-${not_key}}")
    private static String val3;

    @JsonValue("${list:[1,2,3]}")
    private static List<Integer> list;

    @JsonValue("${obj}")
    private static ConfigEntity obj;

    public void test01() {
        System.out.println("val = " + val);
        System.out.println("val1 = " + val1);
        System.out.println("val2 = " + val2);
        System.out.println("val3 = " + val3);
        System.out.println("list = " + list);
        System.out.println("obj = " + obj);
        System.out.println("-------------------------");
    }
}
