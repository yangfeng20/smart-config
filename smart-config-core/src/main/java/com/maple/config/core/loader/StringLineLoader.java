package com.maple.config.core.loader;

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
 * Created Date: 2024/3/12 23:55
 * Description:
 */

public class StringLineLoader extends AbsConfigLoader {
    @Override
    public Collection<ConfigEntity> loaderConfig(String path) {
        List<String> lineDataList;
        try {
            URL resource = this.getClass().getResource("/");
            if (resource == null) {
                throw new RuntimeException("获取路径url为空");
            }
            Path basePath = Paths.get(resource.toURI());
            Path filePath = basePath.resolve(path);
            lineDataList = Files.readAllLines(filePath, Charset.defaultCharset());

        } catch (Exception e) {
            throw new RuntimeException("加载本地配置文件失败", e);
        }


        String desc = "";
        Date createDate = new Date();
        List<ConfigEntity> configEntityList = new ArrayList<>((int) (lineDataList.size() / 0.75) + 1);
        for (String lineStr : lineDataList) {
            if (descInfer && lineStr.startsWith("#")) {
                desc = lineStr.substring(1).trim();
                continue;
            }
            // 过滤注释的配置
            if (!descInfer && lineStr.startsWith("#")) {
                continue;
            }
            // 过滤空行和文件不规范的数据
            if (!lineStr.contains("=")) {
                desc = "";
                continue;
            }

            String[] strArr = lineStr.split("=");
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
