package com.maple.smart.config.core.loader;

import com.maple.smart.config.core.exp.SmartConfigApplicationException;
import com.maple.smart.config.core.model.ConfigEntity;
import com.maple.smart.config.core.model.ReleaseStatusEnum;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author maple
 * @since 2024/3/12 23:55
 * Description:
 */

public class StringLineLoader extends AbsConfigLoader {

    static Pattern pattern = Pattern.compile("\\\\u([0-9a-fA-F]{4})");

    @Override
    public Collection<ConfigEntity> loaderConfig(String path) {
        List<String> lineDataList = readConfigFile(path);

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


    private List<String> readConfigFile(String path) {
        List<String> lineDataList = new ArrayList<>();

        // 不是classpath;绝对路径
        if (!path.startsWith("classpath:")) {
            try {
                URL configFileUrl = Paths.get(path).toUri().toURL();
                Path filePath = Paths.get(configFileUrl.toURI());
                lineDataList = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            } catch (Exception e) {
                throw new SmartConfigApplicationException("failed to load local configuration file [ " + path + " ]", e);
            }
            return lineDataList;
        }


        // classpath
        path = path.substring("classpath:".length());
        if (path.startsWith("/")) {
            path = path.substring(1);
        }

        InputStream inputStream = StringLineLoader.class.getClassLoader().getResourceAsStream(path);
        if (inputStream == null) {
            throw new SmartConfigApplicationException("File [ " + path + " ] does not exist on the current project classpath");
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lineDataList.add(line);
            }
        } catch (Exception e) {
            throw new SmartConfigApplicationException("failed to load local configuration file [ " + path + " ]", e);
        }

        // 处理idea中可能将配置文件编码为ascii码，转换为中文
        return lineDataList.stream()
                .map(StringLineLoader::decodeUnicodeEscapes)
                .collect(Collectors.toList());
    }


    /**
     * 解码 Unicode 转义
     * <p>
     * 解决idea中配置文件编码为ascii码，转换为中文
     * </p>
     *
     * @param input 输入
     * @return {@link String }
     */
    public static String decodeUnicodeEscapes(String input) {
        StringBuilder result = new StringBuilder();
        Matcher matcher = pattern.matcher(input);
        int last = 0;
        while (matcher.find()) {
            result.append(input, last, matcher.start());
            int codePoint = Integer.parseInt(matcher.group(1), 16);
            result.append((char) codePoint);
            last = matcher.end();
        }
        result.append(input.substring(last));
        return result.toString();
    }

}
