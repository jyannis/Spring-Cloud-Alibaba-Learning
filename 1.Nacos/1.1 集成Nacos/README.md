# 目标

将微服务service-a和service-b注册到nacos，以供服务发现。



# 准备工作

1. 下载1.01版本Nacos：

   windows：https://github.com/alibaba/nacos/releases/download/1.0.1/nacos-server-1.0.1.zip

   linux：https://github.com/alibaba/nacos/releases/download/1.0.1/nacos-server-1.0.1.tar.gz



# 流程

1. 启动Nacos
   1. windows启动方式（以下两种都可以）
      1. 双击打开nacos/bin/startup.cmd
      2. cmd中输入cmd startup.cmd启动
   2. linux启动方式
      1. sh startup.sh -m standalone
2. 