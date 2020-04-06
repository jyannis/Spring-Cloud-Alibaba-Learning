# Feign



## 什么是Feign

Feign是Netflix开源的声明式HTTP客户端，致力于让编写HTTP Client更加简单。





## Feign的组成

| 接口                 | 作用                                                         | 默认值                                                       |
| -------------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| `Feign.Builder`      | Feign的入口                                                  | `Feign.Builder`                                              |
| `Client`             | Feign底层用什么去请求                                        | **和Ribbon配合时：**`LoadBalancerFeignClient` **<br>不和Ribbon配合时：**`Fgien.Client.Default` |
| `Contract`           | 契约，注解支持                                               | `SpringMVCContract`                                          |
| `Encoder`            | 解码器，用于将独享转换成HTTP请求消息体                       | `SpringEncoder`                                              |
| `Decoder`            | 编码器，将相应消息体转成对象                                 | `ResponseEntityDecoder`                                      |
| `Logger`             | 日志管理器                                                   | `Slf4jLogger`                                                |
| `RequestInterceptor` | 用于为每个请求添加通用逻辑（拦截器，例子：比如想给每个请求都带上heared） | 无                                                           |



## Feign的日志级别

| 日志级别     | 打印内容                                                     |
| ------------ | ------------------------------------------------------------ |
| NONE（默认） | 不记录任何日志                                               |
| BASIC        | 仅记录请求方法，URL，响应状态代码以及执行时间（适合生产环境） |
| HEADERS      | 记录BASIC级别的基础上，记录请求和响应的header                |
| FULL         | 记录请求和响应的header，body和元数据                         |





## Feign支持的配置项

在本仓库中，只以*Feign*的日志级别为例指导读者如何进行*Feign*的配置（包括*java*代码方式和*yml*属性方式）

*Feign*能够支持的配置项不只有日志级别这一种，还有很多，代码中不会一一列举，就整理在这里：

- *Java*代码方式支持的配置如下：

  | 配置项                           | 作用                                                    |
  | -------------------------------- | ------------------------------------------------------- |
  | Logger.Level                     | 日志级别                                                |
  | Retryer                          | 重试策略                                                |
  | ErrorDecoder                     | 错误解码器                                              |
  | Request.Options                  | 超时时间                                                |
  | Collection\<RequestInterceptor\> | 拦截器                                                  |
  | SetterFactory                    | 用于设置Hystrix的配置属性，<br>Feign整合Hystrix时才会用 |

  

- yml属性方式支持的配置如下：

  ```yaml
  feign.client.config:
  	<feignName>: #微服务名，例如service-b
  		connectTimeout: 5000 #连接超时时间
  		readTimeout: 5000 #读取超时时间
  		loggerLevel: FULL #日志级别
  		errorDecoder: com.example.SimpleErrorDecoder #错误解码器
  		retryer: com.example.SimpleRetryer #重试策略
  		requestInterceptors: com.example.FooRequestInterceptor #拦截器
  		decode404: false #是否对404错误码解码
  		encoder: com.example.SimpleEncoder #编码器
  		decoder: com.example.SimpleDecoder #解码器
  		contract: com.example.SimpleContract #契约
  ```

  



## Feign与RestTemplate比较

在1-2节中，我们依次学会了使用*Nacos*做服务发现组件及使用*Ribbon*做负载均衡。其间都是使用*RestTemplate*来进行远程*Http*调用。

*RestTemplate*是编程式的远程*HTTP*调用，例如下面这段代码：

```java
restTemplate.getForObject(
                "http://service-b/test/{argue}",
                String.class,
                "argue from service-a"
)
```

这样的代码会有一些什么问题呢？

1. 一旦有url或者参数书写错误的bug，IDE或编译器无法给出提示
2. url如果太复杂，会难以维护，写起来也很麻烦
3. 代码可读性不够高，可能为了读懂这一行代码还要去查一下*service-b*下*test/{argue}*的接口文档



而*Feign*的写法：

```java
    /**
     * 当调用这个方法时，feign会调用
     * http://{service-name}/test/{argue}这个url
     * 然后会把响应结果封装进Entity实体返回
     * @param argue
     * @return
     */
    @GetMapping("/test/{argue}")
    Entity methodName(@PathVariable("argue") String argue);
```

非常像我们写常规*SpringBoot*项目时*@RequestMapping*的写法，写起来方便，可读性也强。我们在需要进行远程调用的地方，直接调用*methodName*方法即可。





## Github地址

https://github.com/openfeign/feign