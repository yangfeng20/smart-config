package com.maple.config.core.utils;

import java.util.regex.Pattern;

/**
 * @author maple
 * Created Date: 2024/3/14 13:51
 * Description:
 */

public class SmartConfigConstant {

    public final static Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\$\\{([^}]*)}");
}
