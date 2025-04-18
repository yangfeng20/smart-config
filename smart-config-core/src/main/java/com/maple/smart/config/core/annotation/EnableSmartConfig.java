package com.maple.smart.config.core.annotation;

import com.maple.smart.config.core.spring.PropertySubscriptionInjectBeanPostProcessor;
import com.maple.smart.config.core.spring.SmartConfigSpringContext;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 开启智能配置
 *
 * @author maple
 * @since 2023/12/19 11:12
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({SmartConfigSpringContext.class, PropertySubscriptionInjectBeanPostProcessor.class})
public @interface EnableSmartConfig {

    /**
     * 本地配置文件名<p>默认使用classpath下的application.properties,及resources目录下的application.properties</p>
     * <p>前缀为classpath:表示在编译后的 target/classes下找目标文件; 例如：classpath:application-dev.properties</p>
     * <p>直接写文件名，表示在当前项目目录下找目标文件; 例如：application-dev.properties</p>
     * <p>前缀有根路径，表示使用绝对路径找目标文件; 例如：linux:/home/application-dev.properties windows:D:\\application-dev.properties</p>
     * <p></p>
     * 支持
     * spring.config.location=classpath:application-dev.properties
     * spring.profiles.active=test
     * <p/>
     * 命令行启动参数会覆盖@EnableSmartConfig的值
     *
     * @return 文件路径
     */
    String localFilePath() default "classpath:application.properties";

    /**
     * 配置描述推断
     * 自动根据本地配置文件上的中文注解推断描述
     * <p></p>
     * 启动参数方式
     * -Dsmart.config.desc.infer=true
     *
     * @return 是否开启
     */
    boolean descInfer() default false;

    /**
     * webUi端口
     * <p></p>
     * 启动参数方式
     * -Dsmart.config.webui.port=6767
     *
     * @return 端口
     */
    int webUiPort() default 6767;

    /**
     * 默认值回显
     * <p></p>
     * 代码中使用注解引用的key，在配置文件中没有指定value。而是在代码中使用[:]指定默认值
     * 例如：<pre>{@code
     * @SmartValue("${key:defaultVal}")
     * private String val;
     *
     * @JsonValue("${json-key:{}}")
     * private String jsonVal;
     * }<pre>
     * 如果开启回显，最终 `key` `json-key` 会在webui中回显，并展示默认值
     * <p></p>
     * 启动参数方式
     * -Dsmart.config.default.echo=true
     * @return 是否开启
     */
    boolean defaultValEcho() default false;
}
