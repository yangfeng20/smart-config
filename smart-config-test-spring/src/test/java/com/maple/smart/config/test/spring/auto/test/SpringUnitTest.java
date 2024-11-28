package com.maple.smart.config.test.spring.auto.test;


import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest(classes = SpringAutoTestMain.class)
public class SpringUnitTest {

    @Resource
    private AutoTestConfig01 autoTestConfig01;

    @Resource
    private AutoTestConfig02 autoTestConfig02;


    @Test
    public void testSpring() {
        System.out.println("testSpring");

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
}
