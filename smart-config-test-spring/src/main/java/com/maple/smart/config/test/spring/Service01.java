package com.maple.smart.config.test.spring;

import com.maple.config.core.annotation.JsonValue;
import com.maple.config.core.annotation.SmartValue;
import com.maple.config.core.model.ConfigEntity;
import lombok.Getter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @author maple
 * @since 2023/12/18 15:08
 * Description:
 */

@Getter
@Configuration
public class Service01 {

    @Value("${aa:true}")
    private boolean aa;

    @SmartValue("${aaa:111-${abc}}")
    private String val1;

    @SmartValue("${bbb:222-${ccc}}")
    private String val2;

    // Could not resolve placeholder 'abc' in value "${abc}"
    @Value("${ccc:ddd}")
    private String val3;

    @JsonValue("${list:[]}")
    public List<Integer> list;

    @JsonValue("${obj:{}}")
    public ConfigEntity obj;

    @PostConstruct
    private void init() {
        //System.out.println("list.contains(1) = " + list.contains(1));
        System.out.println("this = " + this);
    }


    @Transactional(rollbackFor = Exception.class)
    public void test01() {
        System.out.println("val1 = " + val1);
        System.out.println("val2 = " + val2);
        System.out.println("val3 = " + val3);
        System.out.println("list = " + list);
        System.out.println("obj = " + obj);
    }


    @Aspect
    @Component
    public static class Aspect01 {
        @Around("execution(* com.maple.smart.config.test.spring.Service01.test01(..))")
        public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
            System.out.println("around - start");
            Object proceed = joinPoint.proceed();
            System.out.println("around - end");
            return proceed;
        }
    }
}
