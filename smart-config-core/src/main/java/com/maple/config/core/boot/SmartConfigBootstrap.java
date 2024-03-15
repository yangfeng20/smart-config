package com.maple.config.core.boot;

import com.maple.config.core.repository.ConfigRepository;

/**
 * @author maple
 * Created Date: 2024/3/13 22:28
 * Description:
 */

public interface SmartConfigBootstrap {

    void init();

    void loaderSpiImpl();

    void loaderConfigToRepository();

    void refreshConfig();

    ConfigRepository getConfigRepository();
}
