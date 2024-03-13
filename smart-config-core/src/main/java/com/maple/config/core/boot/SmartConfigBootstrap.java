package com.maple.config.core.boot;

import com.maple.config.core.control.WebOperationControlPanel;
import com.maple.config.core.listener.ConfigListener;
import com.maple.config.core.loader.ConfigLoader;
import com.maple.config.core.model.ConfigEntity;
import com.maple.config.core.repository.ConfigRepository;
import com.maple.config.core.subscription.ConfigSubscription;
import com.maple.config.core.subscription.LocalConfigSubscription;
import com.maple.config.core.utils.ClassScanner;
import lombok.Setter;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ServiceLoader;

/**
 * @author maple
 * Created Date: 2024/3/13 22:28
 * Description:
 */

public interface SmartConfigBootstrap {

    void init();

    void loaderSpiImpl();

    void loaderConfigToRepository();


    List<Class<?>> scanClass();

    void startWebUi();
}
