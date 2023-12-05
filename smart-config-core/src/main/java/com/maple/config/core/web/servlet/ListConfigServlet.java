package com.maple.config.core.web.servlet;

import com.maple.config.core.api.SmartConfig;
import com.maple.config.core.model.ConfigEntity;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author yangfeng
 * @date : 2023/12/4 16:22
 * desc:
 */
public class ListConfigServlet extends HttpServlet {

    private final SmartConfig smartConfig;

    public ListConfigServlet(SmartConfig smartConfig) {
        this.smartConfig = smartConfig;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
        handler(req, resp);

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handler(req, resp);
    }

    private void handler(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        List<ConfigEntity> configList = smartConfig.configList();

        // 将configList添加到request的属性中
        req.setAttribute("configList", configList);
        // 转发请求到JSP页面
        req.getRequestDispatcher("/configList.jsp").forward(req, resp);
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);


    }
}
