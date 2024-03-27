package com.maple.smart.config.core.web.servlet;

import com.maple.smart.config.core.exp.SmartConfigApplicationException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Objects;

/**
 * @author maple
 * @since 2023/12/20 23:04
 * Description:
 */


public class LoginServlet extends AbsConfigHttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Object username = configRepository.getConfig("smart.username");
        Object pwd = configRepository.getConfig("smart.password");

        String usernameParam = req.getParameter("username");
        String pwdParam = req.getParameter("password");

        if (Objects.equals(usernameParam, username) && Objects.equals(pwdParam, pwd)) {
            // 登录成功，创建session并重定向到首页
            HttpSession session = req.getSession(true);
            resp.sendRedirect("/");
            return;
        }

        throw new SmartConfigApplicationException("Login fail; Incorrect username or password");
    }
}
