# 目标

1. 引入*RocketMQ*完成一次消息的生产




# 流程

### 编写生产者（修改*service-a*）

1. 添加对*RocketMQ*的依赖

   ```xml
   		<dependency>
   			<groupId>org.apache.rocketmq</groupId>
   			<artifactId>rocketmq-spring-boot-starter</artifactId>
               <version>2.0.3</version>
   		</dependency>
   ```

   

2. 修改*yml*配置，指定NameServer地址和Producer Group的名字

   ```yml
   rocketmq:
     name-server: 127.0.0.1:9876
     producer:
       # 必须指定group
       group: group1
   ```

   

3. 写一个消息实体类*BonusPointMessage*

   这里模仿一个用户加积分的业务，实体设计如下

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

   

4. 写一个业务接口

   ```java
   @RestController
   public class TestController {
   
       @Autowired
       private RocketMQTemplate rocketMQTemplate;
   
       @GetMapping("/send")
       public String send(BonusPointMessage bonusPointMessage){
   
           //处理业务
   
           //生产消息，由其他服务来完成用户积分加分的业务逻辑
           //我们把这个消息放在point这一topic下
           rocketMQTemplate.convertAndSend("point",bonusPointMessage);
   
           //正常返回
           return "success";
       }
       
   }
   ```






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

4. 浏览器中访问*localhost:17890*进入*RocketMQ*控制台，点击Message，并选择我们的topic，查询消息：

   ![Message](https://raw.githubusercontent.com/jyannis/Spring-Cloud-Alibaba-Learning/master/4.RocketMQ/4.1%20%E9%9B%86%E6%88%90RocketMQ/docs/Message.png)

   可以看到我们刚才发送的消息已经出现在了这里。

5. 点击Message Detail，查看消息的具体内容：

   ![Message Detail](https://raw.githubusercontent.com/jyannis/Spring-Cloud-Alibaba-Learning/master/4.RocketMQ/4.1%20%E9%9B%86%E6%88%90RocketMQ/docs/Message%20Detail.png)
