package com.maple.config.core.web;


import com.maple.config.core.api.SmartConfig;
import com.maple.config.core.web.filter.GlobalFilter;
import com.maple.config.core.web.servlet.EditConfigServlet;
import com.maple.config.core.web.servlet.HelloServlet;
import com.maple.config.core.web.servlet.ListConfigServlet;
import com.maple.config.core.web.servlet.ReleaseConfigServlet;
import org.apache.tomcat.util.descriptor.web.JspPropertyGroup;
import org.apache.tomcat.util.descriptor.web.JspPropertyGroupDescriptorImpl;
import org.apache.tomcat.util.scan.StandardJarScanner;
import org.eclipse.jetty.apache.jsp.JettyJasperInitializer;
import org.eclipse.jetty.jsp.JettyJspServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.eclipse.jetty.webapp.WebAppContext;

import javax.servlet.DispatcherType;
import javax.servlet.descriptor.JspConfigDescriptor;
import javax.servlet.descriptor.JspPropertyGroupDescriptor;
import javax.servlet.descriptor.TaglibDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;

/**
 * @author yangfeng
 * @date : 2023/12/4 16:11
 * desc:
 */
public class ServerBootstrap {

    private final SmartConfig smartConfig;

    private WebAppContext webAppContext;

    public ServerBootstrap(SmartConfig smartConfig) {
        if (smartConfig == null) {
            throw new IllegalArgumentException();
        }
        this.smartConfig = smartConfig;
    }

    public void start() throws Exception {
        Server server = new Server(8080);
        webAppContext = new WebAppContext();
        webAppContext.setResourceBase("./src/main/web");
        webAppContext.setDisplayName("smart-config");
        webAppContext.setClassLoader(Thread.currentThread().getContextClassLoader());
        webAppContext.setConfigurationDiscovered(true);
        webAppContext.setParentLoaderPriority(true);

        // 添加jsp支持
        webAppContext.addBean(new JspStarter(webAppContext));
        webAppContext.addServlet(JettyJspServlet.class, "*.jsp");


        // 添加一个全局过滤器
        FilterHolder filterHolder = new FilterHolder(new GlobalFilter());
        webAppContext.addFilter(filterHolder, "/*", EnumSet.of(DispatcherType.REQUEST));

        server.setHandler(webAppContext);

        // 添加servlet处理器
        this.addServlet();

        // 设置服务器的处理器
        server.setHandler(webAppContext);

        // 启动服务器
        server.start();
        server.join();
    }

    public void addServlet() {
        webAppContext.addServlet(new ServletHolder(new ListConfigServlet(this.smartConfig)), "/list");
        webAppContext.addServlet(new ServletHolder(new EditConfigServlet(this.smartConfig)), "/edit");
        webAppContext.addServlet(new ServletHolder(new ReleaseConfigServlet(this.smartConfig)), "/release");
    }


    public static class JspStarter extends AbstractLifeCycle implements ServletContextHandler.ServletContainerInitializerCaller {

        JettyJasperInitializer sci;
        ServletContextHandler context;

        public JspStarter(ServletContextHandler context) {
            this.sci = new JettyJasperInitializer();
            this.context = context;
            this.context.setAttribute("org.apache.tomcat.JarScanner", new StandardJarScanner());
        }

        @Override
        protected void doStart() throws Exception {
            ClassLoader old = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(context.getClassLoader());
            try {
                sci.onStartup(null, context.getServletContext());
                super.doStart();
            } finally {
                Thread.currentThread().setContextClassLoader(old);
            }
        }
    }
}
