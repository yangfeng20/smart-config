package com.maple.config.core.loader;

import com.maple.config.core.exp.SmartConfigApplicationException;
import com.maple.config.core.model.ConfigEntity;
import com.maple.config.core.model.ReleaseStatusEnum;

import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @author maple
 * @since 2024/3/12 23:55
 * Description:
 */

public class StringLineLoader extends AbsConfigLoader {
    @Override
    public Collection<ConfigEntity> loaderConfig(String path) {
        List<String> lineDataList;
        URL resource = null;
        if (path.startsWith("classpath:")) {
            path = path.substring("classpath:".length());
            if (path.startsWith("/")) {
                path = path.substring(1);
            }
            resource = ClassLoader.getSystemResource(path);
            if (resource == null) {
                throw new SmartConfigApplicationException("File [ " + path + " ] does not exist on the current project classpath");
            }
        }

        try {
            if (resource == null) {
                resource = Paths.get(path).toUri().toURL();
            }
            Path filePath = Paths.get(resource.toURI());
            lineDataList = Files.readAllLines(filePath, Charset.defaultCharset());

        } catch (Exception e) {
            throw new SmartConfigApplicationException("failed to load local configuration file [ " + path + " ]", e);
        }


        String desc = "";
        Date createDate = new Date();
        List<ConfigEntity> configEntityList = new ArrayList<>((int) (lineDataList.size() / 0.75) + 1);
        String lastRow = "";
        for (int lineInx = 0; lineInx < lineDataList.size(); lineInx++) {
            String lineStr = lastRow + lineDataList.get(lineInx).trim();
            if (configInferDesc && lineStr.startsWith("#")) {
                desc = lineStr.substring(1).trim();
                continue;
            }
            // 过滤注释的配置
            if (!configInferDesc && lineStr.startsWith("#")) {
                continue;
            }

            // 配置文件连字符
            if (lineStr.endsWith("\\") && lineInx + 1 < lineDataList.size() && lineDataList.get(lineInx + 1).startsWith(" ")) {
                lastRow = lineStr.substring(0, lineStr.length() - 1);
                continue;
            } else {
                lastRow = "";
            }

            // 过滤空行和文件不规范的数据
            if (!lineStr.contains("=")) {
                desc = "";
                continue;
            }

            String[] strArr = lineStr.split("=", 2);
            String key = strArr[0].trim();
            String value;
            // 配置文件不规范，只有=号，没有后面的值
            if (strArr.length < 2) {
                value = "";
            } else {
                value = strArr[1].trim();
            }


            ConfigEntity configEntity = new ConfigEntity(key, value, ReleaseStatusEnum.RELEASE.getCode());
            configEntity.setDesc(desc);
            configEntity.setCreateDate(createDate);
            configEntity.setDurable(true);
            configEntityList.add(configEntity);
        }

        return configEntityList;
    }
}
