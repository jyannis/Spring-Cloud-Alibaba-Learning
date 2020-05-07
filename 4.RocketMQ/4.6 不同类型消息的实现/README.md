# 目标

1. 实践三种消息发送模式
   1. 同步（sync）
   2. 异步（async）
   3. 单向（oneWay）
2. 完成消息的**批量发送**（batch）
3. 完成**顺序消息**（order）的发送与消费
4. 完成**过滤消息**（filter）的发送与消费

<br/>

<br/>

<br/>


# 流程

## 实践三种消息发送模式

参考*service-a*下的`SendTypeController`

### 同步发送（sync）

同步发送：

把消息发送到*broker*，必须等*broker*返回了消息接收结果后，才能继续运行后续的代码。

```java
        /**
         * 同步发送消息，需要等待broker响应
         */
        rocketMQTemplate.syncSend(topic,new MyMessage("这是一条同步发送的消息"));
```

<br/>

### 异步发送（async）

异步发送：

把消息发送到*broker*，无需等待*broker*返回接收结果，代码就可以继续往后运行。

当*broker*返回**正确**的接收结果时，会回调`SendCallback`对象的`onSuccess`方法；

当*broker*返回**异常**的接收结果时，会回调`SendCallback`对象的`onException`方法；

```java
        /**
         * 异步发送消息，无需等待broker响应，由专门的回调函数来等待回调
         * onSuccess(SendResult sendResult)在消息发送成功时回调
         * onException(Throwable throwable)在消息发送失败时回调
         */
        rocketMQTemplate.asyncSend(topic,new MyMessage("这是一条异步发送的消息"), new SendCallback() {

            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("异步消息发送成功了，broker得到了正确响应");
                log.info("消息id是：{}",sendResult.getMsgId());
            }

            @Override
            public void onException(Throwable throwable) {
                log.warn("异步消息发送出错，错误为：{}",throwable);
            }
        });
```

<br/>

### 单向发送（oneWay）

单向发送：

把消息发送到*broker*，无需等待*broker*返回接收结果，代码就可以继续往后运行。

并且也不提供回调。相当于直接把broker的接收结果丢弃。

```java
        /**
         * 以单向模式发送消息，无需等待broker响应，也无需回调
         */
        rocketMQTemplate.sendOneWay(topic,new MyMessage("这是一条单向模式发送的消息"));
```

<br/>

<br/>

## 完成消息的批量发送（batch）

参考*service-a*下的`BatchController`

利用`Message`列表即可。

请注意，这里的`Message`是`org.apache.rocketmq.common.message.Message`。

```java
        String topic = "batch";
        List<Message> messages = new ArrayList<>();
        messages.add(new Message(topic, "TagA",  "Hello world 0".getBytes()));
        messages.add(new Message(topic, "TagA",  "Hello world 1".getBytes()));
        messages.add(new Message(topic, "TagA",  "Hello world 2".getBytes()));

        //同步发送消息
        rocketMQTemplate.syncSend(topic,messages);
```

<br/>

<br/>

## 完成顺序消息的发送与消费

1. 顺序消息的发送

   参考*service-a*下的`OrderController`

   `syncSendOrderly`方法的第二个参数是`hashKey`，在相同的`hashKey`下的消息会进入同一个`MessageQueue`（消息队列）。

   同一个`MessageQueue`下的消息是有序的，不同`MessageQueue`的消息之间的顺序是无法保证的。

   ```java
           rocketMQTemplate.syncSendOrderly(topic,"message1","order");
           rocketMQTemplate.syncSendOrderly(topic,"message2","order");
           rocketMQTemplate.syncSendOrderly(topic,"message3","order");
           rocketMQTemplate.syncSendOrderly(topic,"message4","order");
   ```

<br/>

2. 顺序消息的消费

   参考*service-b*下的`OrderListener`

   与一般的消息消费者写法类似，但是要补充一个`consumeMode`配置属性。

   ```java
   @Service
   @RocketMQMessageListener(consumerGroup = "order-group",topic = "order",consumeMode = ConsumeMode.ORDERLY)
   @Slf4j
   public class OrderListener implements RocketMQListener<String>{
   
       @Override
       public void onMessage(String s) {
           log.info("顺序消费消息：message = {}",s);
       }
   }
   ```

   `consumeMode`的两个可选值：

   - ConsumeMode.CONCURRENTLY：并发地接收消息，无法保证消息的消费顺序；

   - ConsumeMode.ORDERLY：顺序地接收消息，可以保证消息的消费顺序。

<br/>

消息的发送和消费代码都写完后，我们来做个[测试](#顺序消息测试)。

<br/>

<br/>

## 完成过滤消息的发送与消费

消息过滤有以下三种方式：

- 基于TAG过滤
- 基于SQL92过滤
- 基于类过滤

这里仅以**基于TAG过滤**来举例，读者有兴趣的话可以自行探索基于SQL92和基于类的过滤方式。

1. 消息的发送

   参考*service-a*下的`TagFilterController`

   我们模拟四条属于不同TAG的消息，其中2条属于`TAGFILTER_ALL`，1条属于`TAGFILTER_A`，1条属于`TAGFILTER_B`

   需要注意的是，为了能够成功过滤，把TAG写在`Message`里是不行的，最重要的是写在syncSend方法的第一个参数`destination`里，写法如下所示：

   ```java
           String topic = "tagFilter";
   
           rocketMQTemplate.syncSend(topic + ":TAGFILTER_ALL",new Message(topic, "TAGFILTER_ALL",  "基础数据1".getBytes()));
           rocketMQTemplate.syncSend(topic + ":TAGFILTER_ALL",new Message(topic, "TAGFILTER_ALL",  "基础数据2".getBytes()));
           rocketMQTemplate.syncSend(topic + ":TAGFILTER_A",new Message(topic, "TAGFILTER_A",  "A类数据".getBytes()));
           rocketMQTemplate.syncSend(topic + ":TAGFILTER_B",new Message(topic, "TAGFILTER_B",  "B类数据".getBytes()));
   ```

<br/>

2. 消息的消费

   参考*service-b*下的`TagFilterListener`

   与一般的消息消费者写法类似，只要在@`RocketMQMessageListener`上添加*selector*相关属性即可。

   ```java
   @Service
   @RocketMQMessageListener(consumerGroup = "tag-filter-group",topic = "tagFilter"
           ,selectorType = SelectorType.TAG,selectorExpression = "TAGFILTER_ALL || TAGFILTER_A")
   @Slf4j
   public class TagFilterListener implements RocketMQListener<String> {
   
   
       @Override
       public void onMessage(String s) {
           log.info("收到消息：message = {}",s);
       }
   }
   ```

   在这里我们过滤出TAG属于`TAGFILTER_ALL`和`TAGFILTER_A`的消息并消费。

<br/>

消息的发送和消费代码都写完后，我们来做个[测试](#过滤消息测试)。

<br/>

<br/>


# 测试

1. 启动*RocketMQ NameServer*和*RocketMQ Broker*

   如何启动请参考[启动RocketMQ](https://github.com/jyannis/Spring-Cloud-Alibaba-Learning/tree/master/4.RocketMQ#windows%E5%AE%89%E8%A3%85%E4%B8%8E%E5%90%AF%E5%8A%A8)

2. 启动*service-a*，*service-b*

<br/>

## 顺序消息测试

1. 调用*service-a*下的*order*服务，返回结果如下：

   注意：C盘要有足够的可用空间，不然可能生产消息失败

   ```
   success
   ```

2. 查看*service-b*的控制台，可以看到日志：

   ```java
   2020-05-07 17:25:38.905  INFO 14596 --- [MessageThread_2] c.j.serviceb.rocketmq.OrderListener      : 顺序消费消息：message = message1
   2020-05-07 17:25:38.905  INFO 14596 --- [MessageThread_2] a.r.s.s.DefaultRocketMQListenerContainer : consume C0A80264042818B4AAC222A379D10020 cost: 0 ms
   2020-05-07 17:25:38.909  INFO 14596 --- [MessageThread_3] c.j.serviceb.rocketmq.OrderListener      : 顺序消费消息：message = message2
   2020-05-07 17:25:38.910  INFO 14596 --- [MessageThread_3] a.r.s.s.DefaultRocketMQListenerContainer : consume C0A80264042818B4AAC222A379D50022 cost: 1 ms
   2020-05-07 17:25:38.910  INFO 14596 --- [MessageThread_3] c.j.serviceb.rocketmq.OrderListener      : 顺序消费消息：message = message3
   2020-05-07 17:25:38.910  INFO 14596 --- [MessageThread_3] a.r.s.s.DefaultRocketMQListenerContainer : consume C0A80264042818B4AAC222A379D70024 cost: 0 ms
   2020-05-07 17:25:38.915  INFO 14596 --- [MessageThread_4] c.j.serviceb.rocketmq.OrderListener      : 顺序消费消息：message = message4
   2020-05-07 17:25:38.916  INFO 14596 --- [MessageThread_4] a.r.s.s.DefaultRocketMQListenerContainer : consume C0A80264042818B4AAC222A379D90026 cost: 1 ms
   ```

   顺序消费消息成功。

<br/>

## 过滤消息测试

1. 调用*service-a*下的*tagFilter*服务，返回结果如下：

   注意：C盘要有足够的可用空间，不然可能生产消息失败

   ```
   success
   ```

2. 查看*service-b*的控制台，可以看到日志：

   ```java
   2020-05-07 17:26:12.604  INFO 14596 --- [essageThread_14] c.j.s.rocketmq.filter.TagFilterListener  : 收到消息：message = {"topic":"tagFilter","flag":0,"properties":{"WAIT":"true","TAGS":"TAGFILTER_ALL"},"body":"5Z+656GA5pWw5o2uMQ==","transactionId":null,"keys":null,"buyerId":null,"waitStoreMsgOK":true,"tags":"TAGFILTER_ALL","delayTimeLevel":0}
   2020-05-07 17:26:12.606  INFO 14596 --- [essageThread_13] c.j.s.rocketmq.filter.TagFilterListener  : 收到消息：message = {"topic":"tagFilter","flag":0,"properties":{"WAIT":"true","TAGS":"TAGFILTER_ALL"},"body":"5Z+656GA5pWw5o2uMg==","transactionId":null,"keys":null,"buyerId":null,"waitStoreMsgOK":true,"tags":"TAGFILTER_ALL","delayTimeLevel":0}
   2020-05-07 17:26:12.608  INFO 14596 --- [essageThread_15] c.j.s.rocketmq.filter.TagFilterListener  : 收到消息：message = {"topic":"tagFilter","flag":0,"properties":{"WAIT":"true","TAGS":"TAGFILTER_A"},"body":"Qeexu+aVsOaNrg==","transactionId":null,"keys":null,"buyerId":null,"waitStoreMsgOK":true,"tags":"TAGFILTER_A","delayTimeLevel":0}
   ```

   可以看到确实只消费了TAG为`TAGFILTER_ALL`和`TAGFILTER_A`的消息，没有消费`TAGFILTER_B`的消息。