package com.maple.smart.config.test.spring.auto.test;

import com.maple.smart.config.core.annotation.JsonValue;
import com.maple.smart.config.core.annotation.SmartValue;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author maple
 * Created Date: 2024/4/2 20:40
 * Description:
 */

@Getter
@Configuration
public class AutoTestConfig01 {

    @Value("${aaa:aaa_default}")
    private String aaa;

    @Value("${bbb:bbb_default-aaa:${aaa}}")
    private String bbb;

    @Value("${ccc}-${ddd:ddd_default}")
    private String ccc;

    @Value("-${eee:eee_default}-")
    private String eee;

    @Value("${fff:fff_default}-${ggg:${iii:}===${jjj:}}-${hhh:hhh_default}")
    private String fff;

    @SmartValue("${aaa:aaa_default}")
    private String smAaa;

    @SmartValue("${bbb:bbb_default-aaa:${aaa}}")
    private String smBbb;

    @SmartValue("${ccc}-${ddd:ddd_default}")
    private String smCcc;

    @SmartValue("-${eee:eee_default}-")
    private String smEee;

    @SmartValue("${fff:fff_default}-${ggg:${iii:}===${jjj:}}-${hhh:hhh_default}")
    private String smFff;

    @JsonValue("${empty.list:[]}")
    private List<String> emptyList;

    @JsonValue("${int.list:[1,2,3]}")
    private List<Integer> intList;

    @JsonValue("${list:[{}]}")
    private List<Object> emptyObjList;

    @JsonValue("${list:[${empty.obj}]}")
    private List<Object> objList;

    @JsonValue("${empty.obj:{}}")
    private Object emptyObj;

    @JsonValue("${obj:{\"aaa\":\"${aaa}\", \"bbb\":\"${bbb:bbb_obj_default}\"}}")
    private Object obj;


}
