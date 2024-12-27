package com.maple.smart.config.core.web.filter;

import com.maple.smart.config.core.infrastructure.json.JSONFacade;
import com.maple.smart.config.core.infrastructure.json.JsonObject;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author maple
 * @since 2023/12/20 22:30
 * Description:
 */

public class AuthFilter implements Filter {

    private final List<String> notAuthUrl = Arrays.asList("/config/login", "/");

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        if (!notAuthUrl.contains(req.getRequestURI())) {
            if (authHandler(req, resp)) {
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }

    private boolean authHandler(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        HttpSession session = req.getSession(false);
        if (session == null) {
            JsonObject response = JSONFacade.createJsonObject();
            response.put("code", 401);
            resp.setContentType("application/json;charset=utf-8");
            resp.getWriter().write(response.toJsonStr());
            return true;
        }

        return false;
    }
}
