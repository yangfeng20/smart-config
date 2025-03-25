package com.maple.smart.config.test.spring;


import com.maple.smart.config.core.annotation.EnableSmartConfig;
import com.maple.smart.config.core.annotation.JsonValue;
import com.maple.smart.config.core.annotation.SmartValue;
import com.maple.smart.config.core.repository.ConfigRepository;
import jakarta.annotation.Resource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author maple
 * @since 2024/3/13 22:00
 * Description:
 */

@RestController
@EnableSmartConfig(descInfer = true)
@EnableAspectJAutoProxy
@SpringBootApplication
public class TestSpringBoot3Application {
    @Resource
    private Service01 service01;


    @SmartValue("${main.test01:1111}")
    private String test01;

    @JsonValue("${main.test02}")
    private Object test02;


    public static void main(String[] args) throws Exception {


        ConfigurableApplicationContext application = SpringApplication.run(TestSpringBoot3Application.class, args);


        System.out.println("application.getBean(Service01.class).list = " + application.getBean(Service01.class).getList());
        ConfigRepository configRepository = application.getBean(ConfigRepository.class);
        System.out.println(configRepository);
    }

    @RequestMapping("/test01")
    public String test01() {
        service01.test01();
        return "success";
    }


}
