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
| FULL         | 记录请求和弦ineader，body和元数据                            |



## Github地址

https://github.com/openfeign/feign







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