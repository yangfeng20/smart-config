package com.maple.smart.config.core.control;

import com.maple.smart.config.core.exp.SmartConfigApplicationException;
import com.maple.smart.config.core.repository.ConfigRepository;
import com.maple.smart.config.core.utils.ClassUtils;
import com.maple.smart.config.core.utils.JarUtils;
import com.maple.smart.config.core.utils.SmartConfigConstant;
import com.maple.smart.config.core.web.filter.AuthFilter;
import com.maple.smart.config.core.web.filter.GlobalFilter;
import com.maple.smart.config.core.web.servlet.AbsConfigHttpServlet;
import com.maple.smart.config.core.web.servlet.BaseConfigHttpServlet;
import lombok.extern.slf4j.Slf4j;
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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.EnumSet;

/**
 * @author maple
 * @since 2024/3/7 22:05
 * Description:
 */
@Slf4j
public class WebOperationControlPanel {

    private final WebAppContext webAppContext;

    public WebOperationControlPanel(ConfigRepository configRepository, Integer port) {
        Server server = new Server(port);
        webAppContext = new WebAppContext();

        try {
            URL webappResourceRootPath = ClassUtils.getClassPathURLByClass(WebOperationControlPanel.class);
            if (webappResourceRootPath.getProtocol().equals("jar")) {
                webappResourceRootPath = buildWebTempRumEnv(port, webappResourceRootPath);
            }
            webAppContext.setBaseResource(Resource.newResource(webappResourceRootPath));
        } catch (Exception e) {
            throw new SmartConfigApplicationException("Failed to build the web temp environment", e);

        }

        webAppContext.setDisplayName("smart-config");
        webAppContext.setAttribute("configRepository", configRepository);
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
        this.addHandler("/config/*", BaseConfigHttpServlet.class);

        // 设置服务器的处理器
        server.setHandler(webAppContext);
    }

    public void start() throws Exception {
        webAppContext.getServer().start();
        //webAppContext.getServer().join();
    }

    public void addHandler(String uri, Class<? extends AbsConfigHttpServlet> handler) {
        webAppContext.addServlet(new ServletHolder(handler), uri);
    }

    private URL buildWebTempRumEnv(Integer port, URL classPath) throws Exception {
        // 获取jar包文件，因为web目录不能在jar包中，所以要解压到临时目录中
        InputStream inputStream;
        // todo 统一获取jarInputStream方式
        if (WebOperationControlPanel.class.getClassLoader().getResource("") == null ||
                !classPath.getPath().contains(SmartConfigConstant.LIB_PATH)) {
            // 前面条件为非springboot环境，后面条件是maven仓库【未打包】
            String jarOriginalPath = ClassUtils.getClassPathURLByClass(WebOperationControlPanel.class).getPath();
            String jarPath;
            if (File.separator.equals("/")) {
                jarPath = jarOriginalPath.replace("file:", "").replace("!/", "");
            } else {
                // windows
                jarPath = jarOriginalPath.replace("file:/", "").replace("!/", "");
            }
            jarPath = URLDecoder.decode(jarPath, StandardCharsets.UTF_8.name());
            inputStream = Files.newInputStream(Paths.get(jarPath));
        } else {
            // jar包 springboot环境
            inputStream = WebOperationControlPanel.class.getClassLoader().getResourceAsStream(SmartConfigConstant.JAR_FILE_PATH);
        }
        if (inputStream == null) {
            throw new SmartConfigApplicationException("Failed to obtain the smart-config web environment jar. path:" + SmartConfigConstant.JAR_FILE_PATH);
        }

        // 构建临时目录文件夹名称
        String tempRumEnvBaseDir = buildTempRumEnvBaseDir(port);
        // 解压jar包到临时文件夹中
        JarUtils.extractJarToDir(inputStream, tempRumEnvBaseDir);

        try {
            inputStream.close();
        } catch (IOException e) {
            log.error("Failed to close inputStream", e);
        }
        if (!tempRumEnvBaseDir.startsWith(File.separator)) {
            tempRumEnvBaseDir = File.separator + tempRumEnvBaseDir;
        }

        return new URL("file:" + tempRumEnvBaseDir.replace(" ", "%20"));
    }

    private String buildTempRumEnvBaseDir(Integer port) {
        String tmpDir = System.getProperty("java.io.tmpdir");
        if (!tmpDir.endsWith(File.separator)) {
            tmpDir += File.separator;
        }
        return tmpDir + "smart-config.jetty." + port + "." + System.currentTimeMillis();
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
