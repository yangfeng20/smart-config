package com.maple.config.test;

import com.maple.config.core.api.LocalFileConfig;
import com.maple.config.core.api.SmartConfig;
import com.maple.config.core.web.ServerBootstrap;

import java.util.ArrayList;

/**
 * @author yangfeng
 * @date : 2023/12/4 9:54
 * desc:
 */

public class TestMain {
    public static void main(String[] args) throws Exception {
        final SmartConfig smartConfig = new LocalFileConfig(true);
        ArrayList<String> list = new ArrayList<>();
        list.add("com.maple.config.test");
        smartConfig.init(list, "application.properties");

        TestService service = new TestService();
        service.test01();


        smartConfig.changeConfig("aaa", "222");
        service.test01();

        new Thread(() -> {
            try {
                new ServerBootstrap(smartConfig).start();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
}
