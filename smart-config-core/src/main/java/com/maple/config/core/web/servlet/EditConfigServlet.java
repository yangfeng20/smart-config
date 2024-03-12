package com.maple.config.core.web.servlet;

import com.maple.config.core.api.SmartConfig;
import com.maple.config.core.model.ConfigEntity;
import com.maple.config.core.model.ReleaseStatusEnum;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

/**
 * @author yangfeng
 * @since : 2023/12/5 20:49
 * desc:
 */
public class EditConfigServlet extends AbsConfigHttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String key = req.getParameter("key");
        String value = req.getParameter("value");
        String isCreateStr = req.getParameter("isCreate");
        boolean isCreate = Boolean.parseBoolean(isCreateStr);
        if (!isCreate && !configRepository.containsKey(key)) {
            throw new IllegalArgumentException();
        }

        if (isCreate) {
            ConfigEntity configEntity = new ConfigEntity(key, value, ReleaseStatusEnum.NOT_RELEASE.getCode());
            configEntity.setCreateDate(new Date());
            configRepository.addConfig(configEntity);
            return;
        }

        if (configRepository.containsKey(key)) {
            ConfigEntity configEntity = configRepository.getConfigEntity(key);
            configEntity.setValue(value);
            configEntity.setUpdateDate(new Date());
            configEntity.setStatus(ReleaseStatusEnum.NOT_RELEASE.getCode());
        }
    }
}
