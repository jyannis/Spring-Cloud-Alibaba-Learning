# 目标

1. 引入*RocketMQ*完成一次消息的消费




# 流程

### 编写消费者（修改*service-b*）

1. 添加对*RocketMQ*的依赖

   ```xml
   		<dependency>
   			<groupId>org.apache.rocketmq</groupId>
   			<artifactId>rocketmq-spring-boot-starter</artifactId>
               <version>2.0.3</version>
   		</dependency>
   ```

   

2. 修改*yml*配置，指定NameServer地址

   ```yml
   rocketmq:
     name-server: 127.0.0.1:9876
   ```
   
   
   
3. 写一个消息实体类*BonusPointMessage*

   ```java
@Data
   @NoArgsConstructor
   @AllArgsConstructor
   @Builder
   public class BonusPointMessage {
   
       /**
        * 待加分的用户id
        */
       private Integer userId;
   
       /**
        * 加的分值
        */
       private Double point;
   
   }
   ```
   
   

4. 写一个监听类（接收并消费*RocketMQ*的消息）

   ```java
   /**
    * 这里@RocketMQMessageListener(consumerGroup = "b-group1",topic = "point")
    * 表明接收的是主题为point的消息，且这个消费者隶属于b-group1消费者组
    */
   @Service
   @RocketMQMessageListener(consumerGroup = "b-group1",topic = "point")
   @Slf4j
   public class BonusPointListener implements RocketMQListener<BonusPointMessage>{
   
       /**
        * 收到消息时调用的方法
        * @param bonusPointMessage 收到的消息
        */
       @Override
       public void onMessage(BonusPointMessage bonusPointMessage) {
           log.info("收到的消息是{}",bonusPointMessage);
       }
   }
   ```






# 测试

1. 启动*RocketMQ NameServer*和*RocketMQ Broker*

   如何启动请参考[启动RocketMQ](https://github.com/jyannis/Spring-Cloud-Alibaba-Learning/tree/master/4.RocketMQ#windows%E5%AE%89%E8%A3%85%E4%B8%8E%E5%90%AF%E5%8A%A8)

2. 启动*service-a*，*service-b*

3. 调用*service-a*下的*send*服务，随意传一个*BonusPointMessage*，返回结果如下：

   注意：C盘要有足够的可用空间，不然可能生产消息失败

   ```
   success
   ```

4. 查看*service-b*的控制台，可以看到日志：

   ```
   2020-04-19 18:08:42.797  INFO 6164 --- [MessageThread_5] c.j.s.rocketmq.BonusPointListener        : 收到的消息是BonusPointMessage(userId=1, point=1.0)
   ```

   消费消息成功。