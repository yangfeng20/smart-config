
# Smart-Config

### 使用
1. spring-boot使用

    主类添加注解
    ```java
    @EnableSmartConfig
    public class XxxApplication {
        ApplicationContext application = SpringApplication.run(XxxApplication.class, args);
    }
    ```

2. 非spring项目使用
    ```java
    public class App{
        public static void main(String[]args){
            SmartConfig smartConfig = new LocalFileConfig(true);
            ArrayList<String> list = new ArrayList<>();
            // 类扫描路径，有@SmartValue的类
            list.add("com.maple.config.test");
            smartConfig.init(list, "application.properties");
        }
    }
    ```

默认web-ui端口：6767


非spring应用仅支持静态变量

本地文件和配置配置的值不同时，优先使用本地文件的值。

web-ui列表展示的为最新数据。包含瞬时配置和本地配置。


todo 先能简单使用。后续在考虑并发，页面体验，异常全局处理。jsp页面不允许直接访问
1. 使用springboot时，@Value所在对象不能被代理。
2. 端口可配置，spring本地文件配置