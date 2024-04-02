package com.maple.smart.config.core.boot;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.core.status.Status;
import com.maple.smart.config.core.exp.SmartConfigApplicationException;
import com.maple.smart.config.core.listener.ConfigListener;
import com.maple.smart.config.core.loader.ConfigLoader;
import com.maple.smart.config.core.repository.ConfigRepository;
import com.maple.smart.config.core.subscription.ConfigSubscription;
import com.maple.smart.config.core.utils.ClassUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * @author maple
 * @since 2024/3/7 22:11
 * Description:
 */

@Slf4j
public class LocalConfigBootstrap extends AbsConfigBootstrap {


    public LocalConfigBootstrap(boolean descInfer, int webUiPort, String localConfigPath, List<String> packagePathList) {
        super(descInfer, webUiPort, localConfigPath, packagePathList);
    }

    @Override
    public void init() {
        // 非springboot应用缺省日志配置文件时设置root日志级别为info
        ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
        if (loggerFactory instanceof ch.qos.logback.classic.LoggerContext) {
            ch.qos.logback.classic.LoggerContext context = (ch.qos.logback.classic.LoggerContext) loggerFactory;
            for (Status status : context.getStatusManager().getCopyOfStatusList()) {
                if (status.getMessage().contains("Setting up default configuration")) {
                    log.debug("Smart-Config 缺省logback.xml配置日志级别为INFO");
                    Logger rootLogger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
                    rootLogger.setLevel(Level.INFO);
                    break;
                }
            }
        }

        super.init();
        scanClassAndSubscription();
        log.debug("Smart-Config 扫描类并添加订阅完成");
        this.refreshConfig();
    }


    @Override
    public void loaderSpiImpl() {

        // 配置加载器
        ServiceLoader.load(ConfigLoader.class).forEach(configLoader -> {
            configLoader.setConfigInferDesc(descInfer);
            configLoaderList.add(configLoader);
        });

        // 配置仓库
        ServiceLoader.load(ConfigRepository.class).forEach(configRepository -> this.configRepository = configRepository);

        // 配置订阅者
        ServiceLoader.load(ConfigSubscription.class).forEach(configSubscription -> {
            this.configSubscription = configSubscription;
            this.configSubscription.setConfigRepository(this.configRepository);
            this.configRepository.setSubscription(configSubscription);
        });

        // 配置监听者
        ServiceLoader.load(ConfigListener.class).forEach(configListener -> {
            configListener.setConfigSubscription(this.configSubscription);
            configSubscription.addListener(configListener);
        });
    }

    /**
     * 扫描类和订阅
     * 非spring应用需要自己手动扫描含有 {@code @SmartValue @JsonValue} 注解的类路径
     * <p/>
     * 找到这些类并添加订阅
     */
    private void scanClassAndSubscription() {
        // 扫描类并添加订阅
        List<Class<?>> scanClass = scanClass();
        for (Class<?> clazz : scanClass) {
            configSubscription.addSubscription(clazz);
        }
    }

    public List<Class<?>> scanClass() {
        if (packagePathList == null || packagePathList.isEmpty()) {
            throw new IllegalArgumentException("please Specify the path of the package name to scan");
        }

        List<Class<?>> scannerResult = new ArrayList<>();
        for (String packagePath : packagePathList) {
            try {
                List<Class<?>> classes = ClassUtils.getClasses(packagePath);
                scannerResult.addAll(classes);
            } catch (ClassNotFoundException | IOException e) {
                throw new SmartConfigApplicationException("Scan class failed in " + packagePath + " packet path", e);
            }
        }
        return scannerResult;
    }
}
