package com.maple.config.core.control;

import com.maple.config.core.repository.ConfigRepository;
import com.maple.config.core.utils.UrlUtil;
import com.maple.config.core.web.filter.AuthFilter;
import com.maple.config.core.web.filter.GlobalFilter;
import com.maple.config.core.web.servlet.*;
import org.apache.tomcat.util.scan.StandardJarScanner;
import org.eclipse.jetty.apache.jsp.JettyJasperInitializer;
import org.eclipse.jetty.jsp.JettyJspServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.WebAppContext;

import javax.servlet.DispatcherType;
import java.net.URL;
import java.util.EnumSet;

/**
 * @author maple
 * Created Date: 2024/3/7 22:05
 * Description:
 */

public class WebOperationControlPanel {

    private final ConfigRepository configRepository;
    private final Server server;

    private final WebAppContext webAppContext;

    public WebOperationControlPanel(ConfigRepository configRepository, Integer port) {
        this.configRepository = configRepository;

        server = new Server(port);
        webAppContext = new WebAppContext();
        webAppContext.setAttribute("configRepository", configRepository);
        URL webappResourceRootPath = UrlUtil.getParentURL(WebOperationControlPanel.class.getClassLoader().getResource("index.html"));
        webAppContext.setBaseResource(Resource.newResource(webappResourceRootPath));
        webAppContext.setDisplayName("smart-config");
        webAppContext.setClassLoader(Thread.currentThread().getContextClassLoader());
        webAppContext.setConfigurationDiscovered(true);
        webAppContext.setParentLoaderPriority(true);

        // 添加jsp支持
        webAppContext.addBean(new WebOperationControlPanel.JspStarter(webAppContext));
        webAppContext.addServlet(JettyJspServlet.class, "*.jsp");

        // 添加一个全局过滤器
        FilterHolder filterHolder = new FilterHolder(new GlobalFilter());
        webAppContext.addFilter(filterHolder, "/*", EnumSet.of(DispatcherType.REQUEST));
        webAppContext.addFilter(new FilterHolder(new AuthFilter()), "/*", EnumSet.of(DispatcherType.REQUEST));
        server.setHandler(webAppContext);

        // 添加servlet处理器
        this.addHandler("/login", LoginServlet.class);
        this.addHandler("/list", ListConfigServlet.class);
        this.addHandler("/edit", EditConfigServlet.class);
        this.addHandler("/release", ReleaseConfigServlet.class);

        // 设置服务器的处理器
        server.setHandler(webAppContext);
    }

    public void start() throws Exception {

        webAppContext.getServer().start();
        webAppContext.getServer().join();

        // 启动服务器
        server.start();
        server.join();
    }

    public void addHandler(String uri, Class<? extends AbsConfigHttpServlet> handler) {
        webAppContext.addServlet(new ServletHolder(handler), uri);
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
