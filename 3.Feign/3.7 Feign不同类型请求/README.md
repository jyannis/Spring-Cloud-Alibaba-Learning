# 目标

1. 使用*Feign*远程调用以下几种请求类型：

   | 请求方式 | 参数类型  | 对应注解      | Feign的使用方式         |
   | -------- | --------- | ------------- | ----------------------- |
   | GET      | path      | @PathVariable | 与SpringMVC使用方式相同 |
   | GET      | query     | @RequestParam | 与SpringMVC使用方式相同 |
   | GET      | form-data | 无注解        | 需要添加@SpringQueryMap |
   | POST     | path      | @PathVariable | 与SpringMVC使用方式相同 |
   | POST     | query     | @RequestParam | 与SpringMVC使用方式相同 |
   | POST     | body      | @RequestBody  | 与SpringMVC使用方式相同 |
   | POST     | form-data | 无注解        | 需要添加@SpringQueryMap |

   借助这个项目，我们可以熟悉Feign远程调用和传统SpringMVC使用方式的联系和区别。



# 流程

1. 为*service-b*添加一个实体类*User*，并添加一些测试接口：

   ***User***：

   ```java
   @Data
   public class User {
   
       private String username;
       private String password;
   
   }
   ```

   ***TestController***：

   ```java
   @RestController
   public class TestController {
   
       /**
        * GET path
        * 路径参数
        * @param argue
        * @return
        */
       @GetMapping("path/{argue}")
       public String path(@PathVariable("argue") String argue){
           return argue;
       }
   
       /**
        * GET query
        * 查询参数
        * @param argue
        * @return
        */
       @GetMapping("query/{argue}")
       public String query(@RequestParam("argue") String argue){
           return argue;
       }
   
       /**
        * GET formdata
        * form-data参数
        * @param user
        * @return
        */
       @GetMapping("form-data")
       public User formdata(User user){
           return user;
       }
   
   
   
       /**
        * POST path
        * 路径参数
        * @param argue
        * @return
        */
       @PostMapping("path/{argue}")
       public String pathPost(@PathVariable("argue") String argue){
           return argue;
       }
   
       /**
        * POST query
        * 查询参数
        * @param argue
        * @return
        */
       @PostMapping("query/{argue}")
       public String queryPost(@RequestParam("argue") String argue){
           return argue;
       }
   
       /**
        * POST body
        * body参数
        * @param user
        * @return
        */
       @PostMapping("body")
       public User bodyPost(@RequestBody User user){
           return user;
       }
   
       /**
        * POST formdata
        * form-data参数
        * @param user
        * @return
        */
       @PostMapping("form-data")
       public User formdataPost(User user){
           return user;
       }
   
   }
   
   ```

   

2. 为*service-a*添加同样的实体类*User*，并设计*ServiceBFeignClient*：

   ***User***：

   ```java
   @Data
   @Builder
   @NoArgsConstructor
   @AllArgsConstructor
   public class User {
   
       private String username;
       private String password;
   
   }
   ```

   ***ServiceBFeignClient***：

   ```java
   @FeignClient(name = "service-b")
   public interface ServiceBFeignClient {
   
       /**
        * GET path
        * 路径参数
        * @param argue
        * @return
        */
       @GetMapping("path/{argue}")
       public String path(@PathVariable("argue") String argue);
   
       /**
        * GET query
        * 查询参数
        * @param argue
        * @return
        */
       @GetMapping("query/{argue}")
       public String query(@RequestParam("argue") String argue);
   
       /**
        * 传实体类型时需要加上@SpringQueryMap
        * GET formdata
        * form-data参数
        * @param user
        * @return
        */
       @GetMapping("form-data")
       public User formdata(@SpringQueryMap User user);
   
   
   
       /**
        * POST path
        * 路径参数
        * @param argue
        * @return
        */
       @PostMapping("path/{argue}")
       public String pathPost(@PathVariable("argue") String argue);
   
       /**
        * POST query
        * 查询参数
        * @param argue
        * @return
        */
       @PostMapping("query/{argue}")
       public String queryPost(@RequestParam("argue") String argue);
   
       /**
        * POST body
        * body参数
        * @param user
        * @return
        */
       @PostMapping("body")
       public User bodyPost(User user);
   
       /**
        * POST formdata
        * form-data参数
        * @param user
        * @return
        */
       @PostMapping("form-data")
       public User formdataPost(@SpringQueryMap User user);
   
   }
   ```

   可以发现*ServiceBFeignClient*中的代码，几乎相当于把*service-b*中的*TestController*复制过来，然后去除每个方法的方法体。

   唯一有区别的就是*form-data*类型参数（也就是无注解情况下传实体类型）需要@*SpringQueryMap*注解

   

3. 写一个测试接口

   ```java
   @RestController
   @Slf4j
   public class RemotingController {
   
       @Autowired
       private ServiceBFeignClient serviceBFeignClient;
   
       @GetMapping("remote")
       public String remote(){
   
           User user = User.builder().username("Jack").password("asd").build();
           log.info(serviceBFeignClient.path("GET path argue"));
           log.info(serviceBFeignClient.query("GET query argue"));
           log.info("GET form-data: {}",serviceBFeignClient.formdata(user));
   
           log.info(serviceBFeignClient.pathPost("POST path argue"));
           log.info(serviceBFeignClient.queryPost("POST query argue"));
           log.info("POST body: {}",serviceBFeignClient.bodyPost(user));
           log.info("POST form-data: {}",serviceBFeignClient.formdataPost(user));
           return "success";
       }
   
   }
   ```

   



# 测试

1. 启动*Nacos*

2. 启动*service-a*，*service-b*

4. 调用*service-a*下的*remote*服务，返回结果如下：


```
2020-04-08 22:51:27.913  INFO 21412 --- [nio-8181-exec-1] com.jyannis.servicea.RemotingController  : GET path argue
2020-04-08 22:51:27.922  INFO 21412 --- [nio-8181-exec-1] com.jyannis.servicea.RemotingController  : GET query argue
2020-04-08 22:51:28.044  INFO 21412 --- [nio-8181-exec-1] com.jyannis.servicea.RemotingController  : GET form-data: User(username=Jack, password=asd)
2020-04-08 22:51:28.048  INFO 21412 --- [nio-8181-exec-1] com.jyannis.servicea.RemotingController  : POST path argue
2020-04-08 22:51:28.052  INFO 21412 --- [nio-8181-exec-1] com.jyannis.servicea.RemotingController  : POST query argue
2020-04-08 22:51:28.108  INFO 21412 --- [nio-8181-exec-1] com.jyannis.servicea.RemotingController  : POST body: User(username=Jack, password=asd)
2020-04-08 22:51:28.111  INFO 21412 --- [nio-8181-exec-1] com.jyannis.servicea.RemotingController  : POST form-data: User(username=Jack, password=asd)
```


