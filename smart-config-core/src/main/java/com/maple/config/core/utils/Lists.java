package com.maple.config.core.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

/**
 * @author maple
 * Created Date: 2024/3/14 10:52
 * Description:
 */

public class Lists {

    @SafeVarargs
    public static <E> ArrayList<E> newArrayList(E... elements) {
        Objects.requireNonNull(elements);
        ArrayList<E> list = new ArrayList<>(elements.length);
        Collections.addAll(list, elements);
        return list;
    }
}
