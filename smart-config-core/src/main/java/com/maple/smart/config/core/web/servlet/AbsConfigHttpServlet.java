package com.maple.smart.config.core.web.servlet;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.maple.smart.config.core.exp.SmartConfigApplicationException;
import com.maple.smart.config.core.model.ConfigEntity;
import com.maple.smart.config.core.model.ConfigVO;
import com.maple.smart.config.core.model.ReleaseStatusEnum;
import com.maple.smart.config.core.repository.ConfigRepository;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

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

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Object data = null;
        JSONObject response = new JSONObject();
        response.put("code", 200);
        response.put("message", "success");
        try {
            if (req.getRequestURI().contains("/list")) {
                data = configRepository.configList().stream().map(ConfigVO::build).collect(Collectors.toList());
            } else if (req.getRequestURI().contains("/save")) {
                ConfigEntity configEntity = getJsonData(req, ConfigEntity.class);
                configEntity.setStatus(ReleaseStatusEnum.NOT_RELEASE.getCode());
                configRepository.addConfig(configEntity);
            } else if (req.getRequestURI().contains("/release")) {
                configRepository.release();
            } else if (req.getRequestURI().contains("/login")) {
                login(req, resp);
            } else {
                resp.sendRedirect("/");
                return;
            }
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", e.getMessage());
        }
        response.put("data", data);
        resp.setContentType("application/json;charset=utf-8");
        resp.getWriter().write(response.toJSONString());
    }

    private void login(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        Object username = configRepository.getConfig("smart.username");
        Object pwd = configRepository.getConfig("smart.password");

        String usernameParam = req.getParameter("username");
        String pwdParam = req.getParameter("password");

        if (Objects.equals(usernameParam, username) && Objects.equals(pwdParam, pwd)) {
            // 登录成功
            HttpSession session = req.getSession(true);
            // 30分钟过期，访问会刷新
            session.setMaxInactiveInterval(30 * 60);
            return;
        }

        throw new SmartConfigApplicationException("Login fail; Incorrect username or password");
    }

    private <T> T getJsonData(HttpServletRequest req, Class<T> clazz) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        BufferedReader reader = req.getReader();
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        return JSON.parseObject(sb.toString(), clazz);
    }
}
