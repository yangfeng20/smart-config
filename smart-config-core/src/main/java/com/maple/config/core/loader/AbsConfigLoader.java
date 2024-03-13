package com.maple.config.core.loader;

import com.maple.config.core.model.ConfigEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author maple
 * Created Date: 2024/3/7 21:28
 * Description:
 */

public abstract class AbsConfigLoader implements ConfigLoader {

    protected boolean configInferDesc;

    @Override
    public void setConfigInferDesc(boolean openConfigInferDesc) {
        this.configInferDesc = openConfigInferDesc;
    }
}
