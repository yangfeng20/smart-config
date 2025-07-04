
# Smart-Config

智能配置：单体应用下的配置中心，支持动态配置,在单体应用下拥有微服务配置中心般的体验。

主要用来解决在单体应用没有配置中心时，想要动态变更配置。可以理解为单机版的Apollo。

### 优势
- 权限校验
- 动态变更配置
- webUi修改配置
- 支持非SpringBoot应用
- 无缝衔接`SpringBoot`应用
- 支持结构化数据（json，集合，对象）
- 轻量级无冗余第三方库
- 内嵌轻量级jetty服务器
- webUi支持中英文切换
- 支持springboot多配置文件
- 支持`spring.config.location`以及`spring.profiles.active`
- 支持启动参数修改webUi端口以及配置描述推断
- webUi支持默认值回显
- 配置持久化，像使用微服务配置中心一样
- 内置多种冲突策略，且可自定义冲突策略
- 预留多种spi扩展接口（配置加载，配置变更，冲突合并...）



### 效果展示

![示例](/images/img_1.png)

![示例](/images/img_2.png)


### 使用

#### 依赖引入

```xml
<dependency>
   <groupId>io.github.yangfeng20</groupId>
   <artifactId>smart-config-core</artifactId>
   <version>1.0.4</version>
</dependency>
```
- springboot3需要做单独的代码兼容，所以版本独立发布。
- 如果使用的是springboot3，请在正常版本后添加[-springboot3]
- 例如：
```xml
<dependency>
   <groupId>io.github.yangfeng20</groupId>
   <artifactId>smart-config-core</artifactId>
   <version>1.0.4-springboot3</version>
</dependency>
```

- 同时由于springboot3中web-starter也引入了jetty依赖
- 会发生依赖冲突，最终类找不到
- 需要引入依赖管理，并指定jetty版本，强制使用jetty 11.0.20版本
```xml
    <dependencyManagement>
        <dependencies>
            <!--smart-config jetty-->
            <dependency>
                <groupId>org.eclipse.jetty</groupId>
                <artifactId>jetty-bom</artifactId>
                <version>11.0.20</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
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
      
      @JsonValue("${list:[1]}")
      private List<Integer> list;
      
      @JsonValue("${config.entity:{}}")
      private XxxEntity entity; 
   
   } 
   ```

2. 非spring项目使用

   创建LocalFileConfig对象，参数分别为webUi的端口，是否推断配置描述。
    ```java
    public class App{
        public static void main(String[]args){
            SmartConfig smartConfig = new LocalFileConfig(6767, true);
            List<String> list = new ArrayList<>();
            list.add("com.maple.smart.config.test");
            AbsConfigBootstrap bootstrap = new LocalConfigBootstrap(true, 6767,"classpath:application.properties", list);
            bootstrap.init();
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
   
       @JsonValue("${list:[1]}")
       private static List<Integer> list;
      
       @JsonValue("${config.entity:{}}")
       private static XxxEntity entity; 
   }
   ```
   

### 注意
- Web-Ui默认端口：6767
- SpringBoot应用配置类被代理后请勿直接访问属性，而是通过getter访问
- 非Spring应用仅支持静态变量。
- 默认值回显开启时，如果key有两个默认值，即在代码中使用直接使用注解引用key，并且在配置文件中不存在。
  ```java
    @JsonValue("${list:[]}")
    public List<Integer> list1;
  
    @JsonValue("${list:[1,2,3]}")
    public List<Integer> list2;
   ```
    - webui中显示的值为最后一次赋值的默认值，这个顺序为容器决定
    - 多个默认值不建议使用默认值回显功能，可能导致webui展示与代码中默认值不一致
- `@Value`暂不支持默认值回显，请使用`@JsonValue`或者`@SmartValue`


### 后续计划
- [x] web配置中心展示默认值配置
- [x] 定时在操作系统临时目录保存一份最新配置；加载时合并
- spel支持
- 环境变量以及jvm参数数据源支持
- [x] 导出配置（最好格式不变）
- yaml格式文件支持
- 配置增加数据来源字段（配置文件，系统临时文件，代码默认值...）

### 测试运行
- 如果要直接跑当前项目的源代码，请先切换到对应分支。springboot3切换到springboot3分支。其他切换到master分支。
- 并在不同的分支选择不同的jdk版本。springboot3选择jdk17以上即可