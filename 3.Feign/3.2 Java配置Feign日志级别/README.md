# 目标

1. 修改调用*service-b*的*FeignClient*的日志级别为FULL






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

1. 添加FeignClient配置类

   ```java
   /**
    * ServiceBFeignClient的Feign配置类
    */
   public class ServiceBFeignConfiguration {
   
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

   

2. 在*ServiceBFeignClient*接口上的*@FeignClient*注解中添加*configuration*属性

   指定*ServiceBFeignClient*将使用*ServiceBFeignConfiguration*的配置

   ```java
   /**
    * 注解@FeignClient指定该类负责对service-b服务的远程调用
    */
   @FeignClient(name = "service-b",configuration = ServiceBFeignConfiguration.class)
   public interface ServiceBFeignClient {
   
       /**
        * 调用bTest方法时，
        * Feign会帮我们转换为请求http://service-b/test/{argue}
        * 再经由Ribbon解析service-b服务的地址
        * 最终请求到service-b的/test/{argue}接口
        * @param argue
        * @return
        */
       @GetMapping("/test/{argue}")
       String bTest(@PathVariable("argue") String argue);
   
   }
   ```

   

3. 在*yml*中配置*ServiceBFeignClient*接口的日志级别为*debug*

   注意，接口的日志级别必须设置为*debug*，如果是*info*的话，前面日志级别不管是设置为*FULL*还是什么都不会进行打印

   ```yaml
   logging:
     level:
       com.jyannis.servicea.feignclient.ServiceBFeignClient: debug
   ```






# 测试

1. 启动*Nacos*

2. 启动*service-a*，*service-b*

4. 调用*service-a*下的*remote*服务，控制台打印出*FeignClient*相关日志如下：


```
2020-04-06 19:40:37.085 DEBUG 10352 --- [nio-8181-exec-2] c.j.s.feignclient.ServiceBFeignClient    : [ServiceBFeignClient#bTest] <--- HTTP/1.1 200 (2434ms)
2020-04-06 19:40:37.085 DEBUG 10352 --- [nio-8181-exec-2] c.j.s.feignclient.ServiceBFeignClient    : [ServiceBFeignClient#bTest] content-length: 47
2020-04-06 19:40:37.086 DEBUG 10352 --- [nio-8181-exec-2] c.j.s.feignclient.ServiceBFeignClient    : [ServiceBFeignClient#bTest] content-type: text/plain;charset=UTF-8
2020-04-06 19:40:37.086 DEBUG 10352 --- [nio-8181-exec-2] c.j.s.feignclient.ServiceBFeignClient    : [ServiceBFeignClient#bTest] date: Mon, 06 Apr 2020 11:40:37 GMT
2020-04-06 19:40:37.086 DEBUG 10352 --- [nio-8181-exec-2] c.j.s.feignclient.ServiceBFeignClient    : [ServiceBFeignClient#bTest] 
2020-04-06 19:40:37.086 DEBUG 10352 --- [nio-8181-exec-2] c.j.s.feignclient.ServiceBFeignClient    : [ServiceBFeignClient#bTest] this is service-b, argue = argue from service-a
2020-04-06 19:40:37.086 DEBUG 10352 --- [nio-8181-exec-2] c.j.s.feignclient.ServiceBFeignClient    : [ServiceBFeignClient#bTest] <--- END HTTP (47-byte body)
```



分析一下上面这段日志：

1. 请求的协议，状态码，花费了多久

   ```
   [ServiceBFeignClient#bTest] <--- HTTP/1.1 200 (2434ms)
   ```

2. HTTP header

   ```
   [ServiceBFeignClient#bTest] content-length: 47
   [ServiceBFeignClient#bTest] content-type: text/plain;charset=UTF-8
   ```

3. 元数据

   ```
   [ServiceBFeignClient#bTest] date: Mon, 06 Apr 2020 11:40:37 GMT
   ```

4. 响应消息体

   ```
   [ServiceBFeignClient#bTest] this is service-b, argue = argue from service-a
   ```

   