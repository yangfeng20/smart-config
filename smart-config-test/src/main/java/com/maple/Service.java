package com.maple;

import org.springframework.beans.factory.annotation.Value;

import java.util.List;

/**
 * @author maple
 * Created Date: 2023/12/18 15:08
 * Description:
 */

@org.springframework.stereotype.Service
public class Service {

    @Value("${aaa:111}")
    private String val1;

    @Value("${bbb:222}")
    private String val2;

    @Value("${ccc:ddd}")
    private String val3;

    @Value("list:[1,2,3]")
    public List<Integer> list;


    public void test01() {
        System.out.println("val1 = " + val1);
        System.out.println("val2 = " + val2);
        System.out.println("val3 = " + val3);
    }
}
