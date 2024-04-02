package com.maple.smart.config.core.utils;


import com.maple.smart.config.core.exp.SmartConfigApplicationException;
import com.maple.smart.config.core.utils.spring.SpringPropertyPlaceholderHelper;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

/**
 * @author maple
 * @since 2024/3/14 21:47
 * Description:
 */

@Slf4j
public class PlaceholderResolverUtils {

    public static List<String> findAllKey(String str) {
        HashSet<String> resultSet = new HashSet<>();
        new SpringPropertyPlaceholderHelper("${", "}",
                ":", true).replacePlaceholders(str, placeholderName -> {
            if (!placeholderName.contains(":")) {
                resultSet.add(placeholderName);
            }
            return null;
        });
        return new ArrayList<>(resultSet);
    }

    public static String findAnyKey(String str) {
        AtomicReference<String> key = new AtomicReference<>();
        AtomicBoolean isReturn = new AtomicBoolean(false);
        SpringPropertyPlaceholderHelper placeholderHelper = new SpringPropertyPlaceholderHelper("${", "}",
                ":", true);

        SpringPropertyPlaceholderHelper.SpringPlaceholderResolver placeholderResolver = placeholderName -> {
            if (isReturn.get()) {
                throw new SmartConfigApplicationException("结束内部调用");
            } else {
                if (placeholderName.contains(":")) {
                    key.set(placeholderName.substring(0, placeholderName.indexOf(":")));
                } else {
                    key.set(placeholderName);
                }
                isReturn.set(true);
            }
            return null;
        };

        try {
            placeholderHelper.replacePlaceholders(str, placeholderResolver);
        } catch (SmartConfigApplicationException ignore) {
            //ignore
        }
        return key.get();
    }

    public static String resolvePlaceholders(String str, Function<String, String> keyResolver) {
        SpringPropertyPlaceholderHelper placeholderHelper = new SpringPropertyPlaceholderHelper("${",
                "}", ":", false);
        return placeholderHelper.replacePlaceholders(str, keyResolver::apply);
    }
}
