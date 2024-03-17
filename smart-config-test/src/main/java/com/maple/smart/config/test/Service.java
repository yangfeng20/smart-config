package com.maple.smart.config.test;

import com.maple.config.core.annotation.JsonValue;
import com.maple.config.core.annotation.SmartValue;

import java.util.List;

/**
 * @author yangfeng
 * @date : 2023/12/4 9:54
 * desc:
 */

public class Service {

    @SmartValue("${aaa:111}")
    private static String val;
    @SmartValue("${bbb:字段值}")
    private static String val1;

    @SmartValue("${ddd:444-${ccc}}")
    private static String val2;

    @JsonValue("${list:[1,2,3,4]}")
    private static List<Integer> list;

    public void test01() {
        System.out.println("val = " + val);
        System.out.println("val1 = " + val1);
        System.out.println("val2 = " + val2);
        System.out.println("list = " + list);
        System.out.println("-------------------------");
    }
}
