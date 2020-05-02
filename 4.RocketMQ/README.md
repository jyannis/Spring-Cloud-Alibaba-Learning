# RocketMQ

## 什么是RocketMQ

Apache RocketMQ是阿里开源的分布式消息中间件，已经捐献给Apache基金会。

<br/>

<br/>

## MQ适用场景

1. 异步处理

   把一些比较耗时，但是不需要阻塞主流程的任务，使用消息队列进行异步处理，提升用户体验。

2. 流量削峰填谷

   例如常见的秒杀场景。利用消息队列以限制待处理的请求数，避免服务直接被流量洪峰打死。

3. 解耦微服务

   例如A微服务调用B微服务，本来A和B存在耦合的关系，一旦B挂掉，那么A也不能正常返回。

   现在引入MQ后，A微服务发一个消息到MQ，由B消费。如果B挂掉，A依然可以正常返回。等B恢复后，再从MQ取出消息去消费即可。

<br/>

<br/>

## 注意事项

1. 本仓库中使用的是RocketMQ4.5.1，有可能不支持JDK9及以上版本，建议使用JDK8。

2. RocketMQ的本地存储路径不能有空格。

<br/>

<br/>

## Windows安装与启动

### 安装

1. 下载项目根目录里resource下的[rocketmq-all-4.5.1](https://github.com/jyannis/Spring-Cloud-Alibaba-Learning/tree/master/resource/rocketmq-all-4.5.1)和[rocketmq-console-ng-1.0.1.jar](https://github.com/jyannis/Spring-Cloud-Alibaba-Learning/blob/master/resource/rocketmq-console-ng-1.0.1.jar)

2. 在环境变量中配置ROCKET_HOME

   ![ROCKETMQ_HOME](https://raw.githubusercontent.com/jyannis/Spring-Cloud-Alibaba-Learning/master/4.RocketMQ/docs/ROCKETMQ_HOME.png)

<br/>

### 启动

启动前请先配置好ROCKETMQ_HOME环境变量：

在“计算机 → 高级系统设置 → 环境变量”中，添加一个变量ROCKETMQ_HOME，其值为你rocketmq文件夹的路径，例如：

```
C:\Users\{你的用户名}\rocketmq-all-4.5.1
```

1. 启动NameServer

   Cmd命令框执行进入至‘MQ文件夹\bin’下，然后执行`start mqnamesrv.cmd`，启动NameServer。成功后会弹出提示框，此框勿关闭。

   ![启动NameServer](https://raw.githubusercontent.com/jyannis/Spring-Cloud-Alibaba-Learning/master/4.RocketMQ/docs/%E5%90%AF%E5%8A%A8NameServer.png)

2. 启动Broker

   Cmd命令框执行进入至‘MQ文件夹\bin’下，然后执行`start mqbroker.cmd -n 127.0.0.1:9876 autoCreateTopicEnable=true`，启动Broker。成功后会弹出提示框，此框勿关闭。

   ![启动Broker](https://raw.githubusercontent.com/jyannis/Spring-Cloud-Alibaba-Learning/master/4.RocketMQ/docs/%E5%90%AF%E5%8A%A8Broker.png)

<br/>

<br/>

## Mac安装与启动

### 安装

1. 前往[下载地址](http://rocketmq.apache.org/release_notes/release-notes-4.5.1/)，下载 `Binary` 文件即可。

   RocketMQ 4.5.1的下载地址：[RocketMQ 4.5.1](https://www.apache.org/dyn/closer.cgi?path=rocketmq/4.5.1/rocketmq-all-4.5.1-bin-release.zip)

2. 解压压缩包

   ```
   unzip rocketmq-all-4.5.1-bin-release.zip
   ```

<br/>

### 启动

1. 切换目录到RocketMQ根目录

   ```
   cd rocketmq-all-4.5.1-bin-release
   ```

2. 启动Name Server

   ```
   nohup sh bin/mqnamesrv &
   ```

   ```
   tail -f ~/logs/rocketmqlogs/namesrv.log
   
   # 如果成功启动，能看到类似如下的日志：
   2019-07-18 17:03:56 INFO main - The Name Server boot success. ...
   ```

3. 启动Broker

   ```
   nohup sh bin/mqbroker -n localhost:9876 &
   ```

   ```
   tail -f ~/logs/rocketmqlogs/broker.log
   
   # 如果启动成功，能看到类似如下的日志：
   2019-07-18 17:08:41 INFO main - The broker[itmuchcomdeMacBook-Pro.local, 192.168.43.197:10911] boot success. serializeType=JSON and name server is localhost:9876
   ```

<br/>

<br/>

## 启动RocketMQ控制台

打开命令行输入`java -jar rocketmq-console-ng-1.0.1.jar`即可。

<br/>

<br/>

## 基本概念

### 1 消息模型（Message Model）

RocketMQ主要由 Producer、Broker、Consumer 三部分组成，其中Producer 负责生产消息，Consumer 负责消费消息，Broker 负责存储消息。Broker 在实际部署过程中对应一台服务器，每个 Broker 可以存储多个Topic的消息，每个Topic的消息也可以分片存储于不同的 Broker。Message Queue 用于存储消息的物理地址，每个Topic中的消息地址存储于多个 Message Queue 中。ConsumerGroup 由多个Consumer 实例构成。

### 2 消息生产者（Producer）

负责生产消息，一般由业务系统负责生产消息。一个消息生产者会把业务应用系统里产生的消息发送到broker服务器。RocketMQ提供多种发送方式，同步发送、异步发送、顺序发送、单向发送。同步和异步方式均需要Broker返回确认信息，单向发送不需要。

### 3 消息消费者（Consumer）

负责消费消息，一般是后台系统负责异步消费。一个消息消费者会从Broker服务器拉取消息、并将其提供给应用程序。从用户应用的⻆度而言提供了两种消费形式：拉取式消费、推动式消费。

### 4 主题（Topic）

表示一类消息的集合，每个主题包含若干条消息，每条消息只能属于一个主题，是RocketMQ进行消息订阅的基本单位。

### 5 代理服务器（Broker Server）

消息中转⻆色，负责存储消息、转发消息。代理服务器在RocketMQ系统中负责接收从生产者发送来的消息并存储、同时为消费者的拉取请求作准备。代理服务器也存储消息相关的元数据，包括消费者组、消费进度偏移和主题和队列消息等。

### 6 名字服务（Name Server）

名称服务充当路由消息的提供者。生产者或消费者能够通过名字服务查找各主题相应的Broker IP列表。多个Namesrv实例组成集群，但相互独立，没有信息交换。

### 7 拉取式消费（Pull Consumer）

Consumer消费的一种类型，应用通常主动调用Consumer的拉消息方法从Broker服务器拉消息、主动权由应用控制。一旦获取了批量消息，应用就会启动消费过程。

### 8 推动式消费（Push Consumer）

Consumer消费的一种类型，该模式下Broker收到数据后会主动推送给消费端，该消费模式一般实时性较高。

### 9 生产者组（Producer Group）

同一类Producer的集合，这类Producer发送同一类消息且发送逻辑一致。如果发送的是事物消息且原始生产者在发送之后崩溃，则Broker服务器会联系同一生产者组的其他生产者实例以提交或回溯消费。

### 10 消费者组（Consumer Group）

同一类Consumer的集合，这类Consumer通常消费同一类消息且消费逻辑一致。消费者组使得在消息消费方面，实现负载均衡和容错的目标变得非常容易。要注意的是，消费者组的消费者实例必须订阅完全相同的Topic。RocketMQ 支持两种消息模式：集群消费（Clustering）和广播消费（Broadcasting）。

### 11 集群消费（Clustering）

集群消费模式下,相同Consumer Group的每个Consumer实例平均分摊消息。

### 12 广播消费（Broadcasting）

广播消费模式下，相同Consumer Group的每个Consumer实例都接收全量的消息。

### 13 普通顺序消息（Normal Ordered Message）

普通顺序消费模式下，消费者通过同一个消费队列收到的消息是有顺序的，不同消息队列收到的消息则可能是无顺序的。

### 14 严格顺序消息（Strictly Ordered Message）

严格顺序消息模式下，消费者收到的所有消息均是有顺序的。

### 15 代理服务器（Broker Server）

消息中转⻆色，负责存储消息、转发消息。代理服务器在RocketMQ系统中负责接收从生产者发送来的消息并存储、同时为消费者的拉取请求作准备。代理服务器也存储消息相关的元数据，包括消费者组、消费进度偏移和主题和队列消息等。

### 16 消息（Message）

消息系统所传输信息的物理载体，生产和消费数据的最小单位，每条消息必须属于一个主题。RocketMQ中每个消息拥有唯一的Message ID，且可以携带具有业务标识的Key。系统提供了通过Message ID和Key查询消息的功能。

### 17 标签（Tag）

为消息设置的标志，用于同一主题下区分不同类型的消息。来自同一业务单元的消息，可以根据不同业务目的在同一主题下设置不同标签。标签能够有效地保持代码的清晰度和连贯性，并优化RocketMQ提供的查询系统。消费者可以根据Tag实现对不同子主题的不同消费逻辑，实现更好的扩展性。