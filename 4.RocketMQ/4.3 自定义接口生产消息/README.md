# 目标

1. 自定义接口生产消息，将主题*topic*等元信息的设置与业务逻辑解耦。

<br/><br/>


# 流程

### 编写生产者（修改*service-a*）

1. 添加对*cloud-stream*的依赖

   ```xml
   		<dependency>
   			<groupId>org.springframework.cloud</groupId>
   			<artifactId>spring-cloud-starter-stream-rocketmq</artifactId>
   		</dependency>
   ```
   

   
2. 添加接口MySource（我的数据源）

   ```java
   public interface MySource {
   
       String MY_OUTPUT = "my-output";
   
       @Output(MY_OUTPUT)
       MessageChannel output();
   
   }
   ```

   

3. 在启动类上添加*@EnableBinding*使*MySource*生效

   ```java
@SpringBootApplication
   @EnableBinding({MySource.class})
   public class ServiceAApplication {
   
   	public static void main(String[] args) {
   		SpringApplication.run(ServiceAApplication.class, args);
   	}
   
   }
   ```
   
   
   
4. 添加相关配置，指定该接口生产的消息隶属于哪个主题topic

   ```yaml
   spring:
     cloud:
       stream:
         bindings:
           myOutput:
             destination: output-topic
   ```



5. 添加测试接口

   ```java
   @RestController
   public class TestController {
   
       @Autowired
       private MySource mySource;
   
       @GetMapping("/send")
       public String send(BonusPointMessage bonusPointMessage){
   
           //处理业务
   
           //生产消息，由其他服务来完成用户积分加分的业务逻辑
           mySource.output()
                   .send(
                           MessageBuilder
                                   .withPayload(bonusPointMessage)
                           .build()
           );
   
           //正常返回
           return "success";
       }
   
   }
   ```

   

   可以发现使用*MySource*的写法和使用*RocketMQTemplate*的写法是相近的。区别在于主题*topic*的设置不在业务逻辑实现了，所以说是”**将主题*topic*等元信息的设置与业务逻辑解耦**“。

   读者可以对比下之前我们使用*RocketMQTemplate*的实现方式：

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

4. 浏览器中访问*localhost:17890*进入*RocketMQ*控制台，点击Message，并选择我们的topic，查询消息：

   ![Message](https://gitee.com/jyannis/doc/raw/master/Spring-Cloud-Alibaba-Learning/4.RocketMQ/Message.png)

   可以看到我们刚才发送的消息已经出现在了这里。

