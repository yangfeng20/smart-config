package com.maple.config.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author maple
 * Created Date: 2024/3/24 1:23
 * Description:
 */

@Data
@AllArgsConstructor
public class Pair<K,V> {

    private K key;

    private V value;
}
