package com.maple.config.core.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * @author maple
 * Created Date: 2024/3/26 14:25
 * Description:
 */

@Slf4j
public class JarUtils {

    public static void extractJarToDir(InputStream inputStream, String targetDir) {

        try (JarInputStream jarInputStream = new JarInputStream(inputStream)) {
            JarEntry entry;
            while ((entry = jarInputStream.getNextJarEntry()) != null) {
                if (!entry.isDirectory()) {
                    File outputFile = new File(targetDir + File.separator + entry.getName());
                    File parentDir = outputFile.getParentFile();
                    if (!parentDir.exists() && !parentDir.mkdirs()) {
                        log.warn("Failed to create directory: " + parentDir.getAbsolutePath());
                    }
                    try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = jarInputStream.read(buffer)) != -1) {
                            fos.write(buffer, 0, bytesRead);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Failed to extract jar to directory: " + targetDir, e);
        }
    }
}
