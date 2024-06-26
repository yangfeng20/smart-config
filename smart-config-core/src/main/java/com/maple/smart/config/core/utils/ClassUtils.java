package com.maple.smart.config.core.utils;

import com.maple.smart.config.core.exp.SmartConfigApplicationException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

/**
 * @author maple
 */

@Slf4j
public class ClassUtils {

    public static URL getClassPathURLByClass(Class<?> clazz) throws Exception {
        URL resource = clazz.getResource("");
        if (resource == null) {
            throw new SmartConfigApplicationException("can not find classpath by class: " + clazz.getName());
        }

        log.debug("getClassPathURLByClass resource path:{}", resource);
        String packageName = clazz.getPackage().getName().replace(".", "/");
        // 去除包路径
        String url = resource.getPath().replace(packageName + "/", "");
        String classPath = resource.getProtocol() + ":" + url;
        log.debug("getClassPathURLByClass classPath:{}", classPath);
        return new URL(classPath);
    }

    public static List<Class<?>> getClasses(String packageName) throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<>();
        List<URL> urls = new ArrayList<>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            if ("jar".equals(resource.getProtocol())) {
                urls.add(resource);
            } else {
                dirs.add(new File(URLDecoder.decode(resource.getFile(), "utf-8")));
            }
        }
        List<Class<?>> classes = new ArrayList<>();

        for (URL url : urls) {
            classes.addAll(findClassesByJar(url));
        }
        for (File directory : dirs) {
            classes.addAll(findClassesByFile(directory, packageName));
        }
        return classes;
    }

    private static List<Class<?>> findClassesByJar(URL url) throws ClassNotFoundException, UnsupportedEncodingException {
        // jar
        String jarOriginalPath = URLDecoder.decode(url.getFile(), "utf-8");
        if (!jarOriginalPath.contains("jar!/")) {
            return Collections.emptyList();
        }
        if (File.separator.equals("/")) {
            jarOriginalPath = jarOriginalPath.replace("file:", "");
        } else {
            // windows
            jarOriginalPath = jarOriginalPath.replace("file:/", "");
        }
        String[] jarPathAndPackagePath = jarOriginalPath.split("jar!/", 2);
        return JarUtils.loaderClassFromJar(jarPathAndPackagePath[0] + "jar", jarPathAndPackagePath[1]);

    }

    private static List<Class<?>> findClassesByFile(File directory, String packageName) throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        if (files == null) {
            return classes;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClassesByFile(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }

    public static String getFullFieldName(@NonNull Field field) {
        return field.getDeclaringClass().getName() + "." + field.getName();
    }
}
