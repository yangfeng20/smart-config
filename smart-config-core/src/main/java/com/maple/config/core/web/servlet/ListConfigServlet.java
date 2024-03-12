package com.maple.config.core.web.servlet;

import com.maple.config.core.api.SmartConfig;
import com.maple.config.core.model.ConfigEntity;
import com.maple.config.core.model.ConfigVO;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yangfeng
 * @date : 2023/12/4 16:22
 * desc:
 */
public class ListConfigServlet extends AbsConfigHttpServlet {

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
        List<ConfigVO> configList = configRepository.configList()
                .stream().map(ConfigVO::build)
                .collect(Collectors.toList());

        // 将configList添加到request的属性中
        req.setAttribute("configList", configList);
        // 转发请求到JSP页面
        req.getRequestDispatcher("/WEB-INF/list.jsp").forward(req, resp);
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }
}
