package com.maple.config.core.web.servlet;

import com.maple.config.core.api.SmartConfig;
import com.maple.config.core.exp.SmartConfigApplicationException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

/**
 * @author maple
 * Created Date: 2023/12/20 23:04
 * Description:
 */

public class LoginServlet extends HttpServlet {

    private final SmartConfig smartConfig;

    public LoginServlet(SmartConfig smartConfig) {
        this.smartConfig = smartConfig;
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Map<String, Object> extMap = smartConfig.getExtMap();
        Object username = extMap.get("username");
        Object pwd = extMap.get("password");

        String usernameParam = req.getParameter("username");
        String pwdParam = req.getParameter("password");

        if (Objects.equals(usernameParam, username) && Objects.equals(pwdParam, pwd)) {
            // 登录成功，创建session并重定向到首页
            HttpSession session = req.getSession(true);
            resp.sendRedirect("/");
            return;
        }

        throw new SmartConfigApplicationException("账号或密码错误");
    }
}
