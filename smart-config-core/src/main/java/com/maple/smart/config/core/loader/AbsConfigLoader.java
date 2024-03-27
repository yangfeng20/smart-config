package com.maple.smart.config.core.loader;

/**
 * @author maple
 * @since 2024/3/7 21:28
 * Description:
 */

public abstract class AbsConfigLoader implements ConfigLoader {

    protected boolean configInferDesc;

    @Override
    public void setConfigInferDesc(boolean openConfigInferDesc) {
        this.configInferDesc = openConfigInferDesc;
    }
}
