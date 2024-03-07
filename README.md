
# Smart-Config

智能配置：单体应用下的动态配置。
主要用来解决在单体应用没有配置中心时，想要动态变更配置。

### 优势
- 权限校验
- 动态变更配置
- webUi修改配置
- 支持非SpringBoot应用
- 无缝衔接`SpringBoot`应用
- 轻量级无冗余第三方库
- 内嵌轻量级jetty服务器



### 效果展示

![示例](/images/img_1.png)

![示例](/images/img.png)


### 使用

#### 依赖引入

```xml
<dependency>
  <groupId>com.maple.smart.config.core</groupId>
  <artifactId>smart-config-core</artifactId>
  <version>1.0-SNAPSHOT</version>
</dependency>
```


1. Spring-Boot使用

    主类添加注解`@EnableSmartConfig`
    ```java
    @EnableSmartConfig
    public class XxxApplication {
        ApplicationContext application = SpringApplication.run(XxxApplication.class, args);
    }
    ```
   
   使用@Value注解
   ```java
   public class AppConfig{
   
   @Value("${configKey}")
   private String value;
   
   } 
   ```

2. 非spring项目使用

   创建LocalFileConfig对象，参数分别为webUi的端口，是否推断配置描述。
    ```java
    public class App{
        public static void main(String[]args){
            SmartConfig smartConfig = new LocalFileConfig(6767, true);
            // 类扫描路径，有@SmartValue的类，以及本地配置文件地址
            smartConfig.init(Collections.singletonList("com.maple.config.test"), "application.properties");
        }
    }   
    ```
   静态类的静态字段使用@SmartValue注解
   ```java
   public class AppConfig{
   
   @SmartValue("配置key:默认值")
   private static String config1;
   
   @SmartValue("biz.name:abc")
   private static String bizName;
   }
   ```
   

### 注意点
- Web-Ui默认端口：6767
- SpringBoot应用@Value所在的对象不支持代理对象。
- 非Spring应用仅支持静态变量。



### 后续计划
- 页面优化体验
- 支持json
- 支持多类型value

# todo
1. 启动模型架构统一，启动方式不一致，没有模版
2. 不支持list，json。
3. 不同环境的配置文件。是否需要支持@Profile