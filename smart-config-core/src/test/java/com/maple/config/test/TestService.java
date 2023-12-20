package com.maple.config.test;

import com.maple.config.core.annotation.SmartValue;

/**
 * @author yangfeng
 * @date : 2023/12/4 9:54
 * desc:
 */

public class TestService {

    @SmartValue("aaa:111")
    private static String val;
    @SmartValue("bbb:字段值")
    private static String val1;

    @SmartValue("ddd:444")
    private static String val2;

    public void test01(){
        System.out.println("val = " + val);
        System.out.println("val1 = " + val1);
        System.out.println("val2 = " + val2);
        System.out.println("-------------------------");
    }
}
