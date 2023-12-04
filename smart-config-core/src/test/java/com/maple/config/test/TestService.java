package com.maple.config.test;

import com.maple.config.core.annotation.SmartValue;
import com.maple.config.core.api.LocalFileConfig;
import com.maple.config.core.api.SmartConfig;

import java.util.ArrayList;

/**
 * @author yangfeng
 * @date : 2023/12/4 9:54
 * desc:
 */

public class TestService {

    @SmartValue("test")
    private static String val;

    public void test01(){
        System.out.println(val);
    }
}
