# Nacos



## 什么是Nacos

简单来说，Nacos是一款服务发现组件，主要解决两个问题：

1. 服务A如何发现服务B
2. 管理微服务的配置



## 官方文档

https://nacos.io/zh-cn/docs/what-is-nacos.html



## 下载地址

各发行版的下载地址：https://github.com/alibaba/nacos/releases

本代码仓库中使用的版本是1.0.1，下载地址：

windows：https://github.com/alibaba/nacos/releases/download/1.0.1/nacos-server-1.0.1.zip

linux：https://github.com/alibaba/nacos/releases/download/1.0.1/nacos-server-1.0.1.tar.gz

如果是windows版，也可以参考项目的resource文件夹，把里面的*nacos*文件夹取出来就可以了。



## 使用方式

每个微服务都需要集成nacos client，借助nacos client与nacos server通信。



