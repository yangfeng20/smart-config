package com.maple.config.test;

import com.maple.config.core.api.impl.local.LocalFileConfig;
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

        // 修改后未发布
        smartConfig.changeConfig("aaa", "222");
        //smartConfig.release(null);
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
