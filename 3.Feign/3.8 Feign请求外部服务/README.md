# 目标

1. 使用*Feign*脱离负载均衡器（*Load Balancer*）远程调用服务。






# 流程

1. 为*service-a*添加一个*ExternalFeignClient*接口，以调用外部服务：

   ```java
   /**
    * 即使是调用服务发现组件外的服务，这个FeignClient也要指定name或者value参数
    * url参数指向的是这个FeignClient要调用的服务地址，一般是{ip}:{port}
    */
   @FeignClient(name = "service-b-external",url = "localhost:8183")
   public interface ExternalFeignClient {
   
       /**
        * GET path
        * 路径参数
        * @param argue
        * @return
        */
       @GetMapping("path/{argue}")
       public String path(@PathVariable("argue") String argue);
   
   }
   ```
   
   同时保留一个*ServiceBFeignClient*接口，是为了测试服务确实不可被发现的：
   
   ```java
   /**
    * 注解@FeignClient指定该类负责对service-b服务的远程调用
    */
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
   
   }
   ```
   
   
   
2. 写两个测试服务，一个通过外部服务调用（*ExternalFeignClient*），一个通过*Nacos*发现并调用（*ServiceBFeignClient*）

   ```java
   @RestController
   @Slf4j
   public class RemotingController {
   
       @Autowired
       private ServiceBFeignClient serviceBFeignClient;
   
       @Autowired
       private ExternalFeignClient externalFeignClient;
   
       @GetMapping("remote1")
       public String remote1(){
           log.info(serviceBFeignClient.path("GET path argue"));
           return "success";
       }
   
       @GetMapping("remote2")
       public String remote2(){
           log.info(externalFeignClient.path("GET path argue"));
           return "success";
       }
   
   }
   ```

   



# 测试

1. **关闭** *Nacos*

2. 启动*service-a*，*service-b*

3. 调用*service-a*下的*remote1*服务，返回错误如下：

   ```
   com.netflix.client.ClientException: Load balancer does not have available server for client: service-b
   ```

   梳理一下这里的逻辑：

   *ServiceBFeignClient*只指定了*FeignClient*的*name*，没有指定*url*。所以在使用它进行远程调用时，它会通过*Spring Cloud*的负载均衡组件获取到一个服务节点并访问。

   那么负载均衡如何获取节点呢？它需要通过服务发现组件，获取到要访问的服务的所有节点，然后调用负载均衡算法得到最终要访问的一个节点。

   那么既然我们现在把*Nacos*关闭了，没有了服务发现组件，负载均衡获取不到节点列表，也就无法为*FeignClient*返回一个具体的节点。因此抛出*ClientException*。

   

4. 调用*service-a*下的*remote2*服务，返回结果如下：

   ```
   success
   ```

   因为*ExternalFeignClient*指定了服务的URL，所以可以直接访问到，这就类似于简单的HttpClient了。这也旧实现了*Feign*脱离负载均衡器（*Load Balancer*）远程调用服务。

   

也许有读者会问，我们明明是关闭了*Nacos*才导致*remote1*调用失败。为什么不是说是脱离服务发现（*Service discovery*）调用服务，而是脱离负载均衡（*Load Balance*）调用服务呢？

再回顾一下上面的报错信息：

```
com.netflix.client.ClientException: Load balancer does not have available server for client: service-b
```

翻译一下：

```
负载均衡器没有找到一个针对service-b的FeignClient可使用的服务
```

负载均衡器找不到节点是报错的**直接原因**，服务无法发现是**间接原因**。所以严格来说，***FeignClient*和服务发现并无关系**。