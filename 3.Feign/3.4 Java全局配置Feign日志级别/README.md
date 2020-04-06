# 目标

1. 基于*Java*代码修改全局的*FeignClient*的日志级别为FULL






# 前置知识

1. 理解Feign的四种日志级别：

   | 日志级别     | 打印内容                                                     |
   | ------------ | ------------------------------------------------------------ |
   | NONE（默认） | 不记录任何日志                                               |
   | BASIC        | 仅记录请求方法，URL，响应状态代码以及执行时间（适合生产环境） |
   | HEADERS      | 记录BASIC级别的基础上，记录请求和响应的header                |
   | FULL         | 记录请求和响应的header，body和元数据                         |





# 流程

在本节中，只涉及对*service-a*编码的修改，不涉及对*service-b*的修改。

1. 添加*FeignClient*全局配置类

   ```java
   /**
    * 全局的Feign配置类
    */
   public class GlobalFeignConfiguration {
   
       @Bean
       public Logger.Level level(){
           /**
            * Feign支持四种日志级别
            * 请参考README.md
            */
           return Logger.Level.FULL;
       }
   
   }
   ```

   

2. 在启动类的*@EnableFeignClients*注解上添加*defaultConfiguration*属性，完成全局的*FeignClient*配置

   ```java
@SpringBootApplication
   @EnableFeignClients(defaultConfiguration = GlobalFeignConfiguration.class)
   public class ServiceAApplication {
   
   	public static void main(String[] args) {
   		SpringApplication.run(ServiceAApplication.class, args);
   	}
   
   }
   
   ```
   
   
   
3. 在*yml*中配置*ServiceBFeignClient*接口所在包的日志级别为*debug*

   注意，接口的日志级别必须设置为*debug*，如果是*info*的话，前面日志级别不管是设置为*FULL*还是什么都不会进行打印

   ```yaml
   logging:
     level:
       com.jyannis.servicea.feignclient: debug
   ```






# 测试

1. 启动*Nacos*

2. 启动*service-a*，*service-b*

4. 调用*service-a*下的*remote*服务，控制台打印出*FeignClient*相关日志如下：


```
2020-04-06 20:15:34.600 DEBUG 22620 --- [nio-8181-exec-1] c.j.s.feignclient.ServiceBFeignClient    : [ServiceBFeignClient#bTest] <--- HTTP/1.1 200 (1596ms)
2020-04-06 20:15:34.600 DEBUG 22620 --- [nio-8181-exec-1] c.j.s.feignclient.ServiceBFeignClient    : [ServiceBFeignClient#bTest] content-length: 47
2020-04-06 20:15:34.600 DEBUG 22620 --- [nio-8181-exec-1] c.j.s.feignclient.ServiceBFeignClient    : [ServiceBFeignClient#bTest] content-type: text/plain;charset=UTF-8
2020-04-06 20:15:34.600 DEBUG 22620 --- [nio-8181-exec-1] c.j.s.feignclient.ServiceBFeignClient    : [ServiceBFeignClient#bTest] date: Mon, 06 Apr 2020 12:15:34 GMT
2020-04-06 20:15:34.600 DEBUG 22620 --- [nio-8181-exec-1] c.j.s.feignclient.ServiceBFeignClient    : [ServiceBFeignClient#bTest] 
2020-04-06 20:15:34.600 DEBUG 22620 --- [nio-8181-exec-1] c.j.s.feignclient.ServiceBFeignClient    : [ServiceBFeignClient#bTest] this is service-b, argue = argue from service-a
2020-04-06 20:15:34.601 DEBUG 22620 --- [nio-8181-exec-1] c.j.s.feignclient.ServiceBFeignClient    : [ServiceBFeignClient#bTest] <--- END HTTP (47-byte body)
```


