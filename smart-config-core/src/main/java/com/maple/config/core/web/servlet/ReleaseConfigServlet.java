package com.maple.config.core.web.servlet;

import com.maple.config.core.api.SmartConfig;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author yangfeng
 * @date : 2023/12/17 0:38
 * desc:
 */

public class ReleaseConfigServlet extends HttpServlet {

    private final SmartConfig smartConfig;

    public ReleaseConfigServlet(SmartConfig smartConfig) {
        this.smartConfig = smartConfig;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        smartConfig.release(null);
    }
}
