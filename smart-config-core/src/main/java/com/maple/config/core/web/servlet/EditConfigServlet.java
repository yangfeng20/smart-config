package com.maple.config.core.web.servlet;

import com.maple.config.core.api.SmartConfig;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author yangfeng
 * @since : 2023/12/5 20:49
 * desc:
 */
public class EditConfigServlet extends HttpServlet {

    private final SmartConfig smartConfig;

    public EditConfigServlet(SmartConfig smartConfig) {
        this.smartConfig = smartConfig;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String key = req.getParameter("key");
        String value = req.getParameter("value");
        String isCreateStr = req.getParameter("isCreate");
        boolean isCreate = Boolean.parseBoolean(isCreateStr);
        if (!isCreate && !smartConfig.containKey(key)) {
            throw new IllegalArgumentException();
        }

        if (isCreate) {
            smartConfig.addConfig(key, value);
            return;
        }
        smartConfig.changeConfig(key, value);
    }
}
