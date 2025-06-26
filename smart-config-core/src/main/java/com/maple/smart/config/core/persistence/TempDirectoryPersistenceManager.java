package com.maple.smart.config.core.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maple.smart.config.core.model.ConfigEntity;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 临时目录持久化实现
 */
public class TempDirectoryPersistenceManager implements ConfigPersistenceManager {
    private static final String TMP_DIR = System.getProperty("java.io.tmpdir");
    private static final String FILE_PREFIX = "smart-config";
    private static final String FILE_SUFFIX = ".json";
    private static final int MAX_HISTORY = 5;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void persist(Collection<ConfigEntity> configList) {
        // todo 每个项目有不同的名字
        String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        String fileName = FILE_PREFIX + "-" + timestamp + FILE_SUFFIX;
        File file = new File(TMP_DIR, fileName);
        try {
            objectMapper.writeValue(file, configList);
            cleanupOldFiles();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Collection<ConfigEntity> load() {
        File latest = getLatestFile();
        if (latest != null && latest.exists()) {
            try {
                ConfigEntity[] arr = objectMapper.readValue(latest, ConfigEntity[].class);
                return Arrays.asList(arr);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Collections.emptyList();
    }

    private File getLatestFile() {
        File dir = new File(TMP_DIR);
        File[] files = dir.listFiles((d, name) -> name.startsWith(FILE_PREFIX) && name.endsWith(FILE_SUFFIX));
        if (files == null || files.length == 0) {
            return null;
        }
        Arrays.sort(files, Comparator.comparing(File::getName).reversed());
        return files[0];
    }

    private void cleanupOldFiles() {
        File dir = new File(TMP_DIR);
        File[] files = dir.listFiles((d, name) -> name.startsWith(FILE_PREFIX) && name.endsWith(FILE_SUFFIX));
        if (files != null && files.length > MAX_HISTORY) {
            Arrays.sort(files, Comparator.comparing(File::getName).reversed());
            for (int i = MAX_HISTORY; i < files.length; i++) {
                try { Files.deleteIfExists(files[i].toPath()); } catch (IOException ignore) {}
            }
        }
    }
}
