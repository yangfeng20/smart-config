package com.maple.smart.config.core.web.filter;

import com.maple.smart.config.core.exp.SmartConfigApplicationException;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class GlobalFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;



        try {
            chain.doFilter(request, response);
        } catch (Exception e) {
            exceptionHandler(req, resp, e);
        }

    }

    @Override
    public void destroy() {
    }



    private void exceptionHandler(HttpServletRequest req, HttpServletResponse resp, Exception e) throws IOException {
        resp.setContentType("text/plain;charset=UTF-8");
        if (e instanceof SmartConfigApplicationException) {
            resp.setStatus(500);
            PrintWriter writer = resp.getWriter();
            writer.write(e.getMessage());
            writer.flush();
            writer.close();
            return;
        }

        throw new RuntimeException(e);
    }
}