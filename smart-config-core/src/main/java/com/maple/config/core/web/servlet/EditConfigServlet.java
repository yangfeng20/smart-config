package com.maple.config.core.web.servlet;

import com.maple.config.core.api.SmartConfig;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author yangfeng
 * @date : 2023/12/5 20:49
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
        String isCreate = req.getParameter("isCreate");
        if (!Boolean.parseBoolean(isCreate) && !smartConfig.containKey(key)){
            // todo 结束
            throw new IllegalArgumentException();
        }
        // todo value应该是复杂对象，而不是简单的value【在properties文件中要如何存储注释】
        smartConfig.changeConfig(key, value);
    }
}
