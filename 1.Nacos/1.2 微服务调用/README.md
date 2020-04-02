# 目标

1. 可以根据服务名获取到该服务的节点列表
2. 可以获取到在服务注册中心注册成功的所有服务名
3. 完成跨服务的远程调用



# 准备工作

1. 保证Nacos在启动状态，且确保两个服务（service-a和service-b）每次启动时都能成功注册到Nacos上




# 流程

## 一、根据服务名获取节点列表

我们在*service-a*中写一个控制器，在里面写一个接口，获取服务名为*service-b*的节点列表。



1. 我们需要使用SpringCloud为我们提供的一个组件**DiscoveryClient**。

   ```java
       //由SpringCloud提供的组件，和Nacos解耦
       //也就是说 如果我们不使用Nacos而是其他的服务发现组件，依然可以使用DiscoveryClient
   	@Autowired
       private DiscoveryClient discoveryClient;
   ```



2. 接下来写一个接口来获取服务名为*service-b*的节点实例

   ```java
       /**
        * 测试服务发现
        * 获取服务名为service-b的所有服务节点实例
        * @return serviceInstanceList
        */    
   	@GetMapping("testInstances")
       public List<ServiceInstance> getInstances(){
            return discoveryClient.getInstances("service-b");
       }
   ```





接下来做**三次测试**：

1. 启动*service-a*，不启动*service-b*，测试接口*testInstances*，得到返回值如下：

   ```json
   []
   ```

2. 启动*service-a*，启动一个*service-b*实例，测试接口*testInstances*，得到返回值如下：

   ```json
   [
     {
       "serviceId": "service-b",
       "host": "192.168.2.101",
       "port": 8182,
       "secure": false,
       "metadata": {
         "nacos.instanceId": "192.168.2.101#8182#DEFAULT#DEFAULT_GROUP@@service-b",
         "nacos.weight": "1.0",
         "nacos.cluster": "DEFAULT",
         "nacos.healthy": "true",
         "preserved.register.source": "SPRING_CLOUD"
       },
       "uri": "http://192.168.2.101:8182"
     }
   ]
   ```

3. 启动*service-a*，启动两个*service-b*实例，测试接口*testInstances*，得到返回值如下：

   ```json
   [
     {
       "serviceId": "service-b",
       "host": "192.168.2.101",
       "port": 8182,
       "secure": false,
       "metadata": {
         "nacos.instanceId": "192.168.2.101#8182#DEFAULT#DEFAULT_GROUP@@service-b",
         "nacos.weight": "1.0",
         "nacos.cluster": "DEFAULT",
         "nacos.healthy": "true",
         "preserved.register.source": "SPRING_CLOUD"
       },
       "uri": "http://192.168.2.101:8182"
     },
     {
       "serviceId": "service-b",
       "host": "192.168.2.101",
       "port": 8183,
       "secure": false,
       "metadata": {
         "nacos.instanceId": "192.168.2.101#8183#DEFAULT#DEFAULT_GROUP@@service-b",
         "nacos.weight": "1.0",
         "nacos.cluster": "DEFAULT",
         "nacos.healthy": "true",
         "preserved.register.source": "SPRING_CLOUD"
       },
       "uri": "http://192.168.2.101:8183"
     }
   ]
   ```

   

至此，*testInstances*接口测试成功。我们可以看到确实是根据服务名获取到了全部的节点列表。





## 二、根据服务名获取节点列表

在这里我们调用**DiscoveryClient**的另一个API：*discoveryClient.getServices()*。

```java
    /**
     * 测试服务发现
     * 获取服务名列表
     * @return serviceNamesList
     */
    @GetMapping("testServices")
    public List<String> getServiceNames(){
        return discoveryClient.getServices();
    }
```



然后启动一下，会看到我们当前启动的服务名列表：

```json
[
  "service-a",
  "service-b"
]
```





## 三、完成跨服务的远程调用

在这里我们借助*RestTemplate*来完成一个简单的远程调用。

1. 在*service-b*中添加一个可供远程调用的服务

   ```java
   @RestController
   public class TestController {
       @GetMapping("test/{argue}")
       public String test(@PathVariable("argue") String argue){
           return "this is service-b, argue = " + argue;
       }
   }
   ```

   

2. 在*service-a*中添加*RestTemplate*组件。在本例中直接加在了*Application*启动类中，当然我们也可以单独写一个*Configuration*类

   ```java
   	@Bean
   	public RestTemplate restTemplate(){
   		return new RestTemplate();
   	}
   ```

   

3. 编写一个接口，里面调用远程服务，编码思路如下：

   1. 借助*discoveryClient*获取到服务名为*service-b*的所有运行中实例列表*instances*
   2. 取出*instances*中的第一个实例的*uri*，并追加*/test/{argue}*字符串，得到*targetUrl*，以定位到*service-b*中的一项具体服务
   3. 为*targetUrl*填入参数，并发起get请求（restTemplate.getForObject）
   4. 返回远程调用结果

   ```java
   @RestController
   @Slf4j
   public class RemotingController {
   
       @Autowired
       private RestTemplate restTemplate;
   
       @Autowired
       private DiscoveryClient discoveryClient;
   
       @GetMapping("remote")
       public String remote(){
           List<ServiceInstance> instances = discoveryClient.getInstances("service-b");
   
           //找到instances中第一个实例的地址（uri）
           String targetUrl = instances.stream()
                   .map(instance -> instance.getUri().toString() + "/test/{argue}")
                   .findFirst()
                   .orElseThrow(() -> new IllegalArgumentException("当前没有实例"));
   
           log.info("请求的目标地址：{}",targetUrl);
   
           //调用service-b的服务
           //用http get请求，并且返回对象
           return restTemplate.getForObject(
                   targetUrl,
                   String.class,
                   "argue from service-a"
           );
   
       }
   
   }
   ```

   

测试一下接口，结果如下：

```
this is service-b, argue = argue from service-a
```

