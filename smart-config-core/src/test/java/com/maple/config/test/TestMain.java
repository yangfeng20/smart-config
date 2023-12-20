package com.maple.config.test;

import com.maple.config.core.api.SmartConfig;
import com.maple.config.core.api.impl.local.LocalFileConfig;

import java.util.Collections;

/**
 * @author yangfeng
 * @date : 2023/12/4 9:54
 * desc:
 */

public class TestMain {
    public static void main(String[] args) throws Exception {
        final SmartConfig smartConfig = new LocalFileConfig(6767, true);
        smartConfig.init(Collections.singletonList("com.maple.config.test"), "application.properties");

        TestService service = new TestService();

        for (int i = 0; i < 1000; i++) {
            service.test01();
            Thread.sleep(10000);
        }

    }
}
