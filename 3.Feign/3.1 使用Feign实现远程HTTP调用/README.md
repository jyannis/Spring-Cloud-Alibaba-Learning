# 目标

1. 使用*Feign*优化下面这段令人满头黑线的代码：

   ```java
           //调用service-b的服务
           //用http get请求，并且返回对象
           return restTemplate.getForObject(
                   "http://service-b/test/{argue}",
                   String.class,
                   "argue from service-a"
           );
   ```





# 流程

在本节中，只涉及对*service-a*编码的修改，不涉及对*service-b*的修改。

1. 添加对*Feign*的依赖

   ```yaml
   <dependency>
   	<groupId>org.springframework.cloud</groupId>
   	<artifactId>spring-cloud-starter-openfeign</artifactId>
   </dependency>
   ```

   

2. 在启动类上添加*@EnableFeignClients*注解

   ```java
   @SpringBootApplication
   @EnableFeignClients
   public class ServiceAApplication
   ```

   

3. 写一个负责对服务*service-b*远程调用的*FeignClient*

   ```java
   /**
    * 注解@FeignClient指定该类负责对service-b服务的远程调用
    */
   @FeignClient(name = "service-b")
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

   

4. 使用*bTest*方法替换我们之前的*RestTemplate*相关代码

   原来是：

   ```java
   @RestController
   public class RemotingController {
   
       @Autowired
       private RestTemplate restTemplate;
   
       @GetMapping("remote")
       public String remote(){
           //调用service-b的服务
           //用http get请求，并且返回对象
           return restTemplate.getForObject(
                   "http://service-b/test/{argue}",
                   String.class,
                   "argue from service-a"
           );
       }
   
   }
   ```

   现在是：

   ```java
   @RestController
   public class RemotingController {
   
       @Autowired
       private ServiceBFeignClient serviceBFeignClient;
   
       @GetMapping("remote")
       public String remote(){
           //调用service-b的服务
           //用http get请求，并且返回对象
           return serviceBFeignClient.bTest("argue from service-a");
       }
   
   }
   ```

   不管是可读性还是可维护性等等都强了很多，也将业务逻辑和远程调用做了一定程度上的解耦。



# 测试

1. 启动Nacos

2. 启动*service-a*，*service-b*

4. 调用*service-a*下的*remote*服务，返回结果如下：


```
this is service-b, argue = argue from service-a
```


