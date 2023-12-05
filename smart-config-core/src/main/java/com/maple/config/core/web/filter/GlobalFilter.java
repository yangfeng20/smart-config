package com.maple.config.core.web.filter;

import javax.servlet.*;
import java.io.IOException;

public class GlobalFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) {
        // 过滤器初始化时调用
        System.out.println("过滤器初始化");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        // 在请求处理之前执行一些操作
        System.out.println("GlobalFilter: 进入过滤器");

        // 继续执行其他过滤器链或者目标servlet
        chain.doFilter(request, response);

        // 在响应返回给客户端之前执行一些操作
        System.out.println("GlobalFilter: 退出过滤器");
    }

    @Override
    public void destroy() {
        // 过滤器销毁时调用
        System.out.println("过滤器销毁");
    }
}