# 目标

1. 使用*Ribbon*替换自定义的负载均衡器
2. 完成*service-a*对*service-b*的负载均衡调用





# 前置知识

1. 熟悉使用RestTemplate进行远程调用（不熟悉请移步[1.2 微服务调用](https://github.com/jyannis/SpringCloud-Alibaba-Learning/tree/master/1.Nacos/1.2%20%E5%BE%AE%E6%9C%8D%E5%8A%A1%E8%B0%83%E7%94%A8)）





# 流程

由于*spring-cloud-alibaba*已经内置了*ribbon*，所以不需要单独为*ribbon*添加依赖。

1. 在*service-a*中，为*RestTemplate*整合*Ribbon*

   修改*Application*启动类中注入*RestTemplate*的部分。

   ```java
   	@Bean
   	@LoadBalanced//整合ribbon
   	public RestTemplate restTemplate(){
   		return new RestTemplate();
   	}
   ```
   
2. 在*service-a*中，修改*RemotingController*，利用Ribbon来为我们完成负载均衡

   此时我们已经不再需要DiscoveryClient，由Ribbon就可以根据服务名自动帮我们找出所有的节点，并调用负载均衡算法，请求相应的目标服务。

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
   
   有了*Ribbon*以后，我们简化了编码流程：
   
   原来是：
   
   1. 找出节点
   2. 调用负载均衡算法挑选节点
   3. 请求服务
   
   现在是：
   
   1. 请求服务
   
   而*Ribbon*的执行流程是：
   
   在代码执行到
   
   ```java
   restTemplate.getForObject(
                   "http://service-b/test/{argue}",
                   String.class,
                   "argue from service-a"
   );
   ```

​		时，*Ribbon*会自动去定位服务名为*service-b*的所有节点，并调用内置的负载均衡算法，挑选一个节点去请求服务。



3. 在*service-b*中，在*TestController*里注入*HttpServletRequest*，打印一个请求url的日志，便于我们测试*service-a*的请求打在了哪个节点上：

   ```java
   @RestController
   @Slf4j
   public class TestController {
   
       @Autowired
       HttpServletRequest request;
   
       @GetMapping("test/{argue}")
       public String test(@PathVariable("argue") String argue){
           log.info("请求的uri是：" + request.getRequestURI());
           return "this is service-b, argue = " + argue;
       }
   
   }
   ```

   



# 测试

1. 启动Nacos

2. 启动*service-a*

3. 启动若干个*service-b*实例（设为不同端口即可）

4. 多次调用*service-a*下的*remote*服务，查看不同*service-b*节点的控制台日志输出：

   本例中我调用了五次*remote*服务，两个service-b节点的控制台日志分别如下：

   ```
   2020-04-04 12:19:17.441  INFO 25020 --- [nio-8182-exec-9] com.jyannis.serviceb.TestController      : 请求的uri是：/test/argue%20from%20service-a
   2020-04-04 12:19:50.847  INFO 25020 --- [nio-8182-exec-5] com.jyannis.serviceb.TestController      : 请求的uri是：/test/argue%20from%20service-a
   2020-04-04 12:24:41.512  INFO 25020 --- [nio-8182-exec-7] com.jyannis.serviceb.TestController      : 请求的uri是：/test/argue%20from%20service-a
   ```
   
   ```
   2020-04-04 12:19:52.987  INFO 100 --- [nio-8183-exec-1] com.jyannis.serviceb.TestController      : 请求的uri是：/test/argue%20from%20service-a
   2020-04-04 12:24:42.357  INFO 100 --- [nio-8183-exec-3] com.jyannis.serviceb.TestController      : 请求的uri是：/test/argue%20from%20service-a
   ```
   
   其中端口为8182的节点接收了三次请求，端口为8183的节点接收了两次请求。说明Ribbon确实帮助我们完成了负载均衡。

