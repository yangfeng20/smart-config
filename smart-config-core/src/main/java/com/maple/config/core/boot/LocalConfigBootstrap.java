package com.maple.config.core.boot;

import com.maple.config.core.control.WebOperationControlPanel;
import com.maple.config.core.exp.SmartConfigApplicationException;
import com.maple.config.core.listener.AutoUpdateConfigListener;
import com.maple.config.core.loader.ConfigLoader;
import com.maple.config.core.repository.ConfigRepository;
import com.maple.config.core.subscription.ConfigSubscription;
import com.maple.config.core.subscription.LocalConfigSubscription;
import com.maple.config.core.utils.ClassScanner;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

/**
 * @author maple
 * Created Date: 2024/3/7 22:11
 * Description:
 */

public class LocalConfigBootstrap {

    /**
     * 配置描述推断
     */
    private boolean descInfer;

    /**
     * WebUI端口
     */
    private int webUiPort;

    /**
     * 本地文件地址
     */
    private String localConfigPath;

    /**
     * 类扫描路径
     */
    private List<String> packagePathList;


    private ConfigRepository configRepository;

    private ConfigSubscription configSubscription = new LocalConfigSubscription();

    public void init() {

        // todo spi 动态加载实现类

        loaderConfig();

        // 扫描类并添加订阅
        List<Class<?>> scanClass = scanClass();
        for (Class<?> clazz : scanClass) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                configSubscription.addSubscription(field);
            }
        }

        // todo 字段赋值 订阅者添加到配置仓库，配置参数发布，通知订阅者
        configSubscription.refresh();

        // 自动更新 todo spi动态添加
        configSubscription.addListener(new AutoUpdateConfigListener());


    }

    private void loaderConfig() {
        // 加载配置
        ServiceLoader<ConfigLoader> configLoaders = ServiceLoader.load(ConfigLoader.class);
        Iterator<ConfigLoader> loaderIterator = configLoaders.iterator();
        if (!loaderIterator.hasNext()) {
            throw new SmartConfigApplicationException("未提供配置加载器【ConfigLoader】");
        }
        ConfigLoader configLoader = loaderIterator.next();
        configRepository.loader(configLoader.loaderConfig());
    }


    List<Class<?>> scanClass() {
        if (packagePathList == null || packagePathList.isEmpty()) {
            throw new IllegalArgumentException("请指定包名路径");
        }

        List<Class<?>> scannerResult = new ArrayList<>();
        for (String packagePath : packagePathList) {
            try {
                List<Class<?>> classes = ClassScanner.getClasses(packagePath);
                scannerResult.addAll(classes);
            } catch (ClassNotFoundException | IOException e) {
                throw new RuntimeException(e);
            }
        }

        return scannerResult;
    }

    private void startWebUi() {
        if (webUiPort == 0) {
            webUiPort = 6767;
        }
        WebOperationControlPanel webOperationControlPanel = new WebOperationControlPanel(configRepository, webUiPort);
        new Thread(() -> {
            try {
                webOperationControlPanel.start();
            } catch (Exception e) {
                throw new RuntimeException("Smart-config:启动webUi失败", e);
            }
        }).start();
    }
}
