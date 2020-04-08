# SpringCloud-Alibaba-Learning
学习SpringCloud Alibaba



**目录**

- [学习提纲](#学习提纲)
  - 1. [***Nacos***](#1\. nacos)
  - 2. [***Ribbon***](#2. ***ribbon***)
  - 3. [***Feign***](#3\. \*\*\*feign\*\*\*)
- [前置知识](#前置知识)
- [Spring-Cloud-Alibaba是什么](#Spring-Cloud-Alibaba是什么)
  - Spring Cloud
  - Spring Cloud Alibaba
  - Spring Cloud Alibaba主要组件
  - 本仓库中主要使用的组件
- 开发环境/生产环境





## 学习提纲

### 1. ***Nacos***

要将一个单体项目拆分为微服务，首先就要能够做到服务注册和发现，否则不同的微服务间是无法互相调用的。而*Nacos*就是由阿里巴巴开源的一款优秀的服务发现组件。



### 2. ***Ribbon***

继微服务间能够互相发现与调用之后，我们紧接着要考虑的就是如何均衡对每个微服务节点的负载。

如果某个服务有若干个硬件性能相同的节点，也许我们会考虑为它们尽可能平均分配负载；

如果某个服务有个硬件资源较好的节点A，还有个硬件资源不太好的节点B，也许我们会考虑把负载往A那里倾斜一些，让B少一些负载。

Ribbon是Netflix开源的负载均衡器，它内置了许多负载均衡算法，并且能够非常方便地集成RestTemplate、Feign等服务调用组件。



### 3. ***Feign***

*RestTemplate*是**编程式**服务调用组件，而Feign是**声明式**服务调用组件，能够使远程服务调用与其他业务逻辑解耦，提升代码的可读性、可维护性。在本节中，我们将使用*Feign*来替代*RestTemplate*，并实践一种面向契约编程解决方案——使用**Feign继承**，以简化我们的代码。





## 前置知识

比较熟练的**SpringBoot**开发经验





## Spring-Cloud-Alibaba是什么

以下简介参考自[Spring Cloud Alibaba 新一代微服务解决方案](https://yq.aliyun.com/articles/740001?utm_content=g_1000095090)



Spring Cloud Alibaba是阿里巴巴提供的微服务开发一站式解决方案，是阿里巴巴开源中间件与Spring Cloud体系的结合。



### Spring Cloud

SpringCloud 是若干个框架的集合，包括 spring-cloud-config、spring-cloud-bus 等近 20 个子项目，提供了服务治理、服务网关、智能路由、负载均衡、断路器、监控跟踪、分布式消息队列、配置管理等领域的解决方案。

Spring Cloud 通过 Spring Boot 风格的封装，屏蔽掉了复杂的配置和实现原理，最终给开发者留出了一套简单易懂、容易部署的分布式系统开发工具包。

一般来说，Spring Cloud 包含以下组件，主要以 Netflix 开源为主：

![Spring Cloud组件](https://github.com/jyannis/SpringCloud-Alibaba-Learning/blob/master/docs/Spring%20Cloud%E7%BB%84%E4%BB%B6.jpg?raw=true)



### Spring Cloud Alibaba

同 Spring Cloud 一样，Spring Cloud Alibaba 也是一套微服务解决方案，包含开发分布式应用微服务的必需组件，方便开发者通过 Spring Cloud 编程模型轻松使用这些组件来开发分布式应用服务。

依托 Spring Cloud Alibaba，您只需要添加一些注解和少量配置，就可以将 Spring Cloud 应用接入阿里微服务解决方案，通过阿里中间件来迅速搭建分布式应用系统。

作为 Spring Cloud 体系下的新实现，Spring Cloud Alibaba 跟官方的组件或其它的第三方实现如 Netflix, Consul，Zookeeper 等对比，具备了更多的功能:

![Spring Cloud各体系](https://github.com/jyannis/SpringCloud-Alibaba-Learning/blob/master/docs/Spring%20Cloud%E5%90%84%E4%BD%93%E7%B3%BB.jpg?raw=true)



### Spring Cloud Alibaba主要组件

这幅图是 Spring Cloud Alibaba 系列组件，其中包含了阿里开源组件，阿里云商业化组件，以及集成Spring Cloud 组件。

![Spring Cloud Alibaba包含组件](https://github.com/jyannis/SpringCloud-Alibaba-Learning/blob/master/docs/Spring%20Cloud%20Alibaba%E5%8C%85%E5%90%AB%E7%BB%84%E4%BB%B6.jpg?raw=true)



### 本仓库中主要使用的组件

如下：

|     组件      |         实现         |
| :-----------: | :------------------: |
|  分布式配置   |        Nacos         |
| 服务注册/发现 |        Nacos         |
|   服务熔断    |       Sentinel       |
|   服务调用    |        Feign         |
|   服务路由    | Spring Cloud Gateway |
|  分布式消息   |       RocketMQ       |
|   负载均衡    |        Ribbon        |





## 开发环境/生产环境

|    **ARTIFACT**    |  **VERSION**  |
| :----------------: | :-----------: |
|        JDK         |       8       |
|     SpringBoot     |     2.1.5     |
|    SpringCloud     | Greenwich.SR1 |
| SpringCloudAlibaba |     0.9.0     |





