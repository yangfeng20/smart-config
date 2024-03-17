package com.maple.config.core.utils;

import javafx.util.Pair;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author maple
 * @since 2024/3/14 21:47
 * Description:
 */

@NoArgsConstructor
@AllArgsConstructor
public class PlaceholderResolver {

    private static final String defPlaceholderPrefix = "${";
    private static final String defPlaceholderSuffix = "}";

    private String placeholderPrefix = defPlaceholderPrefix;
    private String placeholderSuffix = defPlaceholderSuffix;
    private String valueSeparator = ":";
    private boolean ignoreUnresolvablePlaceholders = true;
    private static final Map<String, String> wellKnownSimplePrefixes = new HashMap<>(4);

    static {
        wellKnownSimplePrefixes.put("}", "{");
        wellKnownSimplePrefixes.put("]", "[");
        wellKnownSimplePrefixes.put(")", "(");
    }

    public String resolveText(String input, Function<String, String> keyResolver) {
        return resolveText(input, placeholderPrefix, placeholderSuffix, keyResolver);
    }

    public static String defResolveText(String input, Function<String, String> keyResolver) {
        return resolveText(input, defPlaceholderPrefix, defPlaceholderSuffix, keyResolver);
    }

    private static String resolveText(String text, String prefix, String suffix, Function<String, String> keyResolver) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        int startInx = text.indexOf(prefix);
        if (startInx == -1) {
            return text;
        }
        int endInx = text.lastIndexOf(suffix);
        if (endInx == -1) {
            return text;
        }

        String innerKey = text.substring(startInx + prefix.length(), endInx);
        String placeholderValue = keyResolver.apply(innerKey);
        if (placeholderValue == null) {
            return text;
        }

        String resolvedText = text.replace(prefix + innerKey + suffix, placeholderValue);
        return resolveText(resolvedText, prefix, suffix, keyResolver);
    }

    public static Pair<Boolean, Pair<Integer, Integer>> containsSimplePlaceholder(String text) {
        if (text == null || text.isEmpty()) {
            return new Pair<>(false, null);
        }

        int startIndex = text.indexOf(defPlaceholderPrefix);
        if (startIndex == -1) {
            return new Pair<>(false, null);
        }

        int endIndex = text.lastIndexOf(defPlaceholderSuffix);
        if (endIndex == -1) {
            return new Pair<>(false, null);
        }

        String innerKey = text.substring(startIndex + defPlaceholderPrefix.length(), endIndex);
        return new Pair<>(true, new Pair<>(startIndex + defPlaceholderPrefix.length(), endIndex));
    }
}
