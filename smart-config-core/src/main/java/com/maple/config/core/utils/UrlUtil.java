package com.maple.config.core.utils;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author maple
 * @since 2024/3/12 23:15
 * Description:
 */

public class UrlUtil {

    public static URL getParentURL(URL originalURL) {
        if (originalURL == null) {
            return null;
        }
        String urlString = originalURL.toString();
        int lastIndex = urlString.lastIndexOf('/');
        if (lastIndex != -1) {
            String parentURLString = urlString.substring(0, lastIndex + 1);
            try {
                return new URL(parentURLString);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
