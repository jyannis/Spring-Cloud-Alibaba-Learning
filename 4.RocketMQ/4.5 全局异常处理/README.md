# 目标

1. 写一个全局异常处理器，在对于任何消息进行消费的过程中发生异常时，进行捕获并处理。

<br/><br/>


# 流程

### 编写消费者（修改*service-b*）

1. 写一个全局异常处理器GlobalExceptionHandler

   ```java
   @Service
   @Slf4j
   public class GlobalExceptionHandler {
   
       /**
        * 全局异常处理
     * @param message
        */
       @StreamListener("errorChannel")
       public void handleError(Message<?> message){
           ErrorMessage errorMessage = (ErrorMessage) message;
           log.error("发现异常：{}",errorMessage);
       }
   
   }
   ```
   
   
   
2. 在消息消费的方法体里抛出一个异常，进行测试

   ```java
   @Service
   @Slf4j
   public class MySinkConsumer {
   
       @StreamListener(MySink.MY_INPUT)
       public void receive(BonusPointMessage bonusPointMessage){
           log.info("收到消息：message={}", bonusPointMessage);
           throw new RuntimeException("运行时异常");
       }
       
   }
   ```


<br/>

<br/>


# 测试

1. 启动*RocketMQ NameServer*和*RocketMQ Broker*

   如何启动请参考[启动RocketMQ](https://github.com/jyannis/Spring-Cloud-Alibaba-Learning/tree/master/4.RocketMQ#windows%E5%AE%89%E8%A3%85%E4%B8%8E%E5%90%AF%E5%8A%A8)

2. 启动`rocketmq-console-ng-1.0.1.jar`

   如何启动请参考[启动RocketMQ控制台](https://github.com/jyannis/Spring-Cloud-Alibaba-Learning/tree/master/4.RocketMQ#windows%E5%AE%89%E8%A3%85%E4%B8%8E%E5%90%AF%E5%8A%A8)

3. 调用*service-a*下的*send*服务，随意传一个*BonusPointMessage*，返回结果如下：

   注意：C盘要有足够的可用空间，不然可能生产消息失败

   ```
   success
   ```

4. 启动*service-b*，可以看到控制台输出以下信息

   ```
2020-05-02 17:50:38.638 ERROR 18912 --- [MessageThread_1] c.j.s.rocketmq.GlobalExceptionHandler    : 发现异常：ErrorMessage [payload=org.springframework.messaging.MessagingException: Exception thrown while invoking com.jyannis.serviceb.rocketmq.MySinkConsumer#receive[1 args]; nested exception is java.lang.RuntimeException: 运行时异常, failedMessage=GenericMessage [payload=byte[24], headers={rocketmq_QUEUE_ID=3, rocketmq_TOPIC=output-topic, rocketmq_FLAG=0, rocketmq_MESSAGE_ID=C0A802640B3C18B4AAC208FA34500000, rocketmq_SYS_FLAG=0, id=31486bab-63df-ec57-b712-1ebe2ee7be5b, rocketmq_BORN_HOST=192.168.2.100, contentType=application/json, rocketmq_BORN_TIMESTAMP=1588413015121, timestamp=1588413035569}], headers={id=91fe00cc-dc8e-d081-db49-0dcb8f16cf69, timestamp=1588413038635}]
   ```
   
   说明异常捕获并处理成功。

