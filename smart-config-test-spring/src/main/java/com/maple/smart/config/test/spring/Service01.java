package com.maple.smart.config.test.spring;

import com.maple.config.core.annotation.JsonValue;
import com.maple.config.core.annotation.SmartValue;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author maple
 * Created Date: 2023/12/18 15:08
 * Description:
 */

@Service
public class Service01 {

    @Value("${aaa:111-${bbb}}")
    private String val1;

    @SmartValue("${bbb:222}")
    private String val2;

    @Value("${ccc:ddd}")
    private String val3;

    @JsonValue("${list:[1,2,3]}")
    public List<Integer> list;


    @Transactional(rollbackFor = Exception.class)
    public void test01() {
        System.out.println("val1 = " + val1);
        System.out.println("val2 = " + val2);
        System.out.println("val3 = " + val3);
        System.out.println("list = " + list);
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
