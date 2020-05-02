# 目标

1. 自定义接口消费消息。

<br/><br/>


# 流程

### 编写消费者（修改*service-b*）

1. 添加对*cloud-stream*的依赖

   ```xml
   		<dependency>
   			<groupId>org.springframework.cloud</groupId>
   			<artifactId>spring-cloud-starter-stream-rocketmq</artifactId>
   		</dependency>
   ```
   

   
2. 添加接口MySink

   ```java
   public interface MySink {
   
       String MY_INPUT = "myInput";
   
       @Input(MY_INPUT)
       MessageChannel input();
   
   }
   ```

   

3. 在启动类上添加*@EnableBinding*使*MySink*生效

   ```java
@SpringBootApplication
   @EnableBinding({MySink.class})
   public class ServiceBApplication {
   
   	public static void main(String[] args) {
   		SpringApplication.run(ServiceBApplication.class, args);
   	}
   
   }
   ```
   
   
   
4. 添加相关配置，指定该接口消费哪个主题的的消息；该消费者属于哪个消费者组

   ```yaml
   spring:
     cloud:
       stream:
         bindings:
           myInput:
             destination: output-topic
             # 如果是rocketMQ就一定要设置group
             # 其他消息队列可以不设置
             group: group-one
   ```



5. 添加消息监听类（负责消费消息）

   ```java
   @Service
   @Slf4j
   public class MySinkConsumer {
   
       @StreamListener(MySink.MY_INPUT)
       public void receive(BonusPointMessage bonusPointMessage){
           log.info("收到消息：message={}", bonusPointMessage);
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
2020-05-02 13:00:19.046  INFO 25008 --- [MessageThread_1] c.j.serviceb.rocketmq.MySinkConsumer     : 收到消息：message=BonusPointMessage(userId=1, point=1.0)
   ```
   
   说明消息消费成功。

