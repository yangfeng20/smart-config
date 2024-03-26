package com.maple.config.core.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * @author maple
 * Created Date: 2024/3/26 14:25
 * Description:
 */

@Slf4j
public class JarUtils {

    /**
     * 需要拷贝到临时运行目录的文件【仅拷贝web相关文件】
     */
    private static final List<String> cpFileNames = Lists.newArrayList(
            "static/**", "WEB-INF/**", "index.html", "login.html"
    );

    public static void extractJarToDir(InputStream inputStream, String targetDir) {
        PathMatcher pathMatcher = new AntPathMatcher();

        try (JarInputStream jarInputStream = new JarInputStream(inputStream)) {
            JarEntry entry;
            while ((entry = jarInputStream.getNextJarEntry()) != null) {
                JarEntry fEntry = entry;
                if (entry.isDirectory() || cpFileNames.stream().noneMatch(s -> pathMatcher.match(s, fEntry.getName()))) {
                    continue;
                }
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
        } catch (Exception e) {
            log.warn("Failed to extract jar to directory: " + targetDir, e);
        }
    }
}
