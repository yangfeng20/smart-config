package com.maple.config.core.web.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author yangfeng
 * @date : 2023/12/17 0:38
 * desc:
 */

public class ReleaseConfigServlet extends AbsConfigHttpServlet {


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        configRepository.release();
    }
}
