# 目标

1. 使用yml属性配置的方式来修改全局的*FeignClient*的日志级别为FULL






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

1. 修改yml文件，补充对*service-b*的*FeignClient*的配置

   ```yaml
   feign:
     client:
       config:
         # 全局配置
         default:
           loggerLevel: FULL
   ```
   
   
   




# 测试

1. 启动*Nacos*

2. 启动*service-a*，*service-b*

4. 调用*service-a*下的*remote*服务，控制台打印出*FeignClient*相关日志如下：


```
2020-04-06 20:22:21.878 DEBUG 19132 --- [nio-8181-exec-3] c.j.s.feignclient.ServiceBFeignClient    : [ServiceBFeignClient#bTest] ---> GET http://service-b/test/argue%20from%20service-a HTTP/1.1
2020-04-06 20:22:21.879 DEBUG 19132 --- [nio-8181-exec-3] c.j.s.feignclient.ServiceBFeignClient    : [ServiceBFeignClient#bTest] ---> END HTTP (0-byte body)
2020-04-06 20:22:22.020 DEBUG 19132 --- [nio-8181-exec-3] c.j.s.feignclient.ServiceBFeignClient    : [ServiceBFeignClient#bTest] <--- HTTP/1.1 200 (140ms)
2020-04-06 20:22:22.020 DEBUG 19132 --- [nio-8181-exec-3] c.j.s.feignclient.ServiceBFeignClient    : [ServiceBFeignClient#bTest] content-length: 47
2020-04-06 20:22:22.020 DEBUG 19132 --- [nio-8181-exec-3] c.j.s.feignclient.ServiceBFeignClient    : [ServiceBFeignClient#bTest] content-type: text/plain;charset=UTF-8
2020-04-06 20:22:22.020 DEBUG 19132 --- [nio-8181-exec-3] c.j.s.feignclient.ServiceBFeignClient    : [ServiceBFeignClient#bTest] date: Mon, 06 Apr 2020 12:22:22 GMT
2020-04-06 20:22:22.020 DEBUG 19132 --- [nio-8181-exec-3] c.j.s.feignclient.ServiceBFeignClient    : [ServiceBFeignClient#bTest] 
2020-04-06 20:22:22.021 DEBUG 19132 --- [nio-8181-exec-3] c.j.s.feignclient.ServiceBFeignClient    : [ServiceBFeignClient#bTest] this is service-b, argue = argue from service-a
2020-04-06 20:22:22.021 DEBUG 19132 --- [nio-8181-exec-3] c.j.s.feignclient.ServiceBFeignClient    : [ServiceBFeignClient#bTest] <--- END HTTP (47-byte body)
```


