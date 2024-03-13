package com.maple;

import com.maple.config.core.boot.LocalConfigBootstrap;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yangfeng
 * @since : 2023/12/4 9:54
 * desc:
 */

public class TestMain {
    public static void main(String[] args) throws Exception {

        LocalConfigBootstrap bootstrap = new LocalConfigBootstrap();
        bootstrap.setDescInfer(true);
        List<String> list = new ArrayList<>();
        list.add("com.maple.config.test.TestMain");
        bootstrap.setPackagePathList(list);
        bootstrap.setLocalConfigPath("application.properties");
        bootstrap.init();

        Service service = new Service();

        for (int i = 0; i < 1000; i++) {
            service.test01();
            Thread.sleep(10000);
        }

    }
}
