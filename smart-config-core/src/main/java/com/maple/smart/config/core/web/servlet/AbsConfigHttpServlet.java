package com.maple.smart.config.core.web.servlet;

import com.fasterxml.jackson.databind.JsonNode;
import com.maple.smart.config.core.exp.SmartConfigApplicationException;
import com.maple.smart.config.core.export.ConfigExporter;
import com.maple.smart.config.core.infrastructure.json.JSONFacade;
import com.maple.smart.config.core.infrastructure.json.JsonObject;
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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
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
        if (req.getRequestURI().contains("/export")) {
            String type = req.getParameter("type");
            if (type == null || type.isEmpty()) {
                type = "properties";
            }
            String content;
            String fileName = "smart-config-export." + type;
            String contentType;
            ConfigExporter exporter;
            switch (type.toLowerCase()) {
                case "json":
                    exporter = new com.maple.smart.config.core.export.JsonConfigExporter();
                    contentType = "application/json;charset=utf-8";
                    break;
                case "yaml":
                case "yml":
                    exporter = new com.maple.smart.config.core.export.YamlConfigExporter();
                    contentType = "application/x-yaml;charset=utf-8";
                    break;
                case "properties":
                default:
                    exporter = new com.maple.smart.config.core.export.PropertiesConfigExporter();
                    contentType = "text/plain;charset=utf-8";
            }
            content = exporter.export(configRepository.configList());
            resp.setContentType(contentType);
            resp.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            resp.getWriter().write(content);
            return;
        }
        super.doGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Object data = null;
        JsonObject response = JSONFacade.createJsonObject();
        response.put("code", 200);
        response.put("message", "success");
        try {
            if (req.getRequestURI().contains("/list")) {
                data = configRepository.configList().stream()
                        .sorted(Comparator.comparing(ConfigEntity::getUpdateDate, Comparator.nullsLast(Comparator.reverseOrder()))
                                .thenComparing(ConfigEntity::getCreateDate, Comparator.reverseOrder()))
                        .map(ConfigVO::build).collect(Collectors.toList());
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
        resp.getWriter().write(response.toJsonStr());
    }

    private void login(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        Object username = generateSha256(configRepository.getConfig("smart.username"));
        Object pwd = generateSha256(configRepository.getConfig("smart.password"));

        JsonNode loginParam = getJsonData(req, JsonNode.class);
        String usernameParam = loginParam.get("username").asText();
        String pwdParam = loginParam.get("pwd").asText();

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

        return JSONFacade.parseObject(sb.toString(), clazz);
    }

    private String generateSha256(String data) {
        try {
            // 创建 MessageDigest 实例并指定算法为 SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            // 对数据进行加密
            byte[] hashedData = digest.digest(data.getBytes());

            // 将加密后的字节数组转换为十六进制字符串
            StringBuilder hexString = new StringBuilder();
            for (byte hashByte : hashedData) {
                String hex = Integer.toHexString(0xff & hashByte);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new SmartConfigApplicationException("不支持该加密算法", e);
        }
    }
}
