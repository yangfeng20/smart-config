package com.maple.config.core.web.filter;

import com.maple.config.core.exp.SmartConfigApplicationException;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.StreamCorruptedException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author maple
 * Created Date: 2023/12/20 22:30
 * Description:
 */

public class AuthFilter implements Filter {

    private final List<String> notAuthUrl = Arrays.asList("/login", "/");

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        if (!notAuthUrl.contains(req.getRequestURI())) {
            authHandler(req, resp);
        }

        filterChain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }

    private void authHandler(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        HttpSession session = req.getSession(false);
        if (session == null) {
            req.getRequestDispatcher("/login.html").forward(req, resp);
        }
    }
}
