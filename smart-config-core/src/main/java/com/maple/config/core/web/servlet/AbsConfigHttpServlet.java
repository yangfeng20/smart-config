package com.maple.config.core.web.servlet;

import com.maple.config.core.repository.ConfigRepository;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

/**
 * @author maple
 * @since 2024/3/12 22:46
 * Description:
 */

public abstract class AbsConfigHttpServlet extends HttpServlet {

    protected ConfigRepository configRepository;

    @Override
    public void init(ServletConfig config) throws ServletException {
        // 通过ServletConfig获取ServletContext
        ServletContext context = config.getServletContext();

        // 尝试从ServletContext中获取名为"configRepository"的对象
        configRepository = (ConfigRepository) context.getAttribute("configRepository");


        // 调用父类的初始化方法
        super.init(config);
    }
}
