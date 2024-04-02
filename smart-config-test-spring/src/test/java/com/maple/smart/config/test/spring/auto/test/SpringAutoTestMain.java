package com.maple.smart.config.test.spring.auto.test;

import com.maple.smart.config.core.annotation.EnableSmartConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author maple
 * Created Date: 2024/4/2 20:37
 * Description:
 */

@RestController
@EnableSmartConfig
@SpringBootApplication
public class SpringAutoTestMain {

    @Resource
    private AutoTestConfig01 autoTestConfig01;
    
    @Resource
    private AutoTestConfig02 autoTestConfig02;

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(SpringAutoTestMain.class, args);
        SpringAutoTestMain springAutoTestMain = context.getBean(SpringAutoTestMain.class);
        springAutoTestMain.test01();
        springAutoTestMain.test02();
    }

    public void test01() {
        String aaa = autoTestConfig01.getAaa();
        String bbb = autoTestConfig01.getBbb();
        String ccc = autoTestConfig01.getCcc();
        String eee = autoTestConfig01.getEee();
        String fff = autoTestConfig01.getFff();
        String smAaa = autoTestConfig01.getSmAaa();
        String smBbb = autoTestConfig01.getSmBbb();
        String smCcc = autoTestConfig01.getSmCcc();
        String smEee = autoTestConfig01.getSmEee();
        String smFff = autoTestConfig01.getSmFff();
        List<String> emptyList = autoTestConfig01.getEmptyList();
        List<Integer> intList = autoTestConfig01.getIntList();
        List<Object> emptyObjList = autoTestConfig01.getEmptyObjList();
        List<Object> objList = autoTestConfig01.getObjList();
        Object emptyObj = autoTestConfig01.getEmptyObj();
        Object obj = autoTestConfig01.getObj();

        System.out.println("aaa = " + aaa);
        System.out.println("bbb = " + bbb);
        System.out.println("ccc = " + ccc);
        System.out.println("eee = " + eee);
        System.out.println("fff = " + fff);
        System.out.println("smAaa = " + smAaa);
        System.out.println("smBbb = " + smBbb);
        System.out.println("smCcc = " + smCcc);
        System.out.println("smEee = " + smEee);
        System.out.println("smFff = " + smFff);
        System.out.println("emptyList = " + emptyList);
        System.out.println("intList = " + intList);
        System.out.println("emptyObjList = " + emptyObjList);
        System.out.println("objList = " + objList);
        System.out.println("emptyObj = " + emptyObj);
        System.out.println("obj = " + obj);
        System.out.println("======================================================================================");
    }
    public void test02() {
        String aaa = autoTestConfig02.getAaa();
        String bbb = autoTestConfig02.getBbb();
        String ccc = autoTestConfig02.getCcc();
        String eee = autoTestConfig02.getEee();
        String fff = autoTestConfig02.getFff();
        String smAaa = autoTestConfig02.getSmAaa();
        String smBbb = autoTestConfig02.getSmBbb();
        String smCcc = autoTestConfig02.getSmCcc();
        String smEee = autoTestConfig02.getSmEee();
        String smFff = autoTestConfig02.getSmFff();
        List<String> emptyList = autoTestConfig02.getEmptyList();
        List<Integer> intList = autoTestConfig02.getIntList();
        List<Object> emptyObjList = autoTestConfig02.getEmptyObjList();
        List<Object> objList = autoTestConfig02.getObjList();
        Object emptyObj = autoTestConfig02.getEmptyObj();
        Object obj = autoTestConfig02.getObj();

        System.out.println("aaa = " + aaa);
        System.out.println("bbb = " + bbb);
        System.out.println("ccc = " + ccc);
        System.out.println("eee = " + eee);
        System.out.println("fff = " + fff);
        System.out.println("smAaa = " + smAaa);
        System.out.println("smBbb = " + smBbb);
        System.out.println("smCcc = " + smCcc);
        System.out.println("smEee = " + smEee);
        System.out.println("smFff = " + smFff);
        System.out.println("emptyList = " + emptyList);
        System.out.println("intList = " + intList);
        System.out.println("emptyObjList = " + emptyObjList);
        System.out.println("objList = " + objList);
        System.out.println("emptyObj = " + emptyObj);
        System.out.println("obj = " + obj);
        System.out.println("======================================================================================");
    }
}
