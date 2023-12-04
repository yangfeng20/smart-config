package com.maple.config.test;

import com.maple.config.core.api.LocalFileConfig;
import com.maple.config.core.api.SmartConfig;
import com.maple.config.core.utils.ClassScanner;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yangfeng
 * @date : 2023/12/4 9:54
 * desc:
 */

public class TestMain {
    public static void main(String[] args) throws Exception {
        SmartConfig smartConfig = new LocalFileConfig();
        ArrayList<String> list = new ArrayList<>();
        list.add("com.maple.config.test");
        smartConfig.registerListener(list);

        TestService service = new TestService();
        service.test01();


        smartConfig.changeConfig("test", "2222");
        service.test01();
    }
}
