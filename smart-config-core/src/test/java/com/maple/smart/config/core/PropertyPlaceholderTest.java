package com.maple.smart.config.core;

import com.maple.smart.config.core.utils.spring.SpringPropertyPlaceholderHelper;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * @author maple
 * Created Date: 2024/4/2 15:44
 * Description:
 */


public class PropertyPlaceholderTest {

    public static void main(String[] args) {

        Properties properties = new Properties();
        properties.put("bbb", "2222");

        Set<String> set = new HashSet<>();
        SpringPropertyPlaceholderHelper springPropertyPlaceholderHelper = new SpringPropertyPlaceholderHelper("${", "}", ":", false);
        String result = springPropertyPlaceholderHelper.replacePlaceholders("111=${aaa:111-${abc:1}-${bdc:${bbb:1}}}+2", new SpringPropertyPlaceholderHelper.SpringPlaceholderResolver() {
            @Override
            public String resolvePlaceholder(String placeholderName) {
                return properties.getProperty(placeholderName);
            }
        });

        System.out.println(result);
    }

}
