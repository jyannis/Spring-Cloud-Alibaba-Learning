# 目标

1. 理解rocketmq中分布式事务消息的实现逻辑
2. 编码实现一个简单的分布式事务

<br/>

<br/>

# 分布式事务实现原理

在rocketmq中，分布式事务的实现思路，简单来说主要是这样的：

1. producer发一条**半消息**到MQ Server（也就是broker）

   **半消息**可以理解为是一种特殊的消息

2. broker返回半消息的接收结果
3. producer开始执行本地事务
4. producer向broker发消息进行**二次确认**
   1. 如果本地事务执行成功，producer就发出“提交”的确认信息，broker就投递消息给consumer
   2. 如果本地事务执行失败，producer就发出“回滚”的确认信息，broker就直接丢弃消息



那么在实际生产中，以上的逻辑中还是会存在漏洞的，主要在于以下情形：**producer发送半消息成功后宕机，没能发出二次确认**

如果发送这个情况，半消息就会一直“死”在broker中，没有被投递到consumer，也没有被丢弃。

所以为了解决这个极端场景下的问题，又引出了几步：

在producer长时间未发送**二次确认**时：

1. broker向producer发起回查请求
2. producer检查本地事务状态（是否成功完成）
3. producer向broker发消息进行**二次确认**（与之前的第四步同理）

<br/>

最后，这个流程可以总结为下图：

![分布式事务流程图-ch](https://gitee.com/jyannis/doc/raw/master/Spring-Cloud-Alibaba-Learning/4.RocketMQ/%E5%88%86%E5%B8%83%E5%BC%8F%E4%BA%8B%E5%8A%A1%E6%B5%81%E7%A8%8B%E5%9B%BE-ch.png)

<br/>

<br/>


# 编码实现

### 编写生产者（修改*service-a*）

1. 添加对@Transactional注解的支持，以模拟本地事务

   它和我们对rocketmq分布式事务的实现并没有直接关系。只是因为既然我们要编码实现分布式事务，那当然要把本地的事务也做好，不然就没有意义了，所以才添加进来。

   ```xml
   		<!--为了获取@Transactional事务注解添加如下依赖-->
   		<dependency>
   			<groupId>org.springframework</groupId>
   			<artifactId>spring-tx</artifactId>
   			<version>5.0.8.RELEASE</version>
   		</dependency>
   ```

   

2. 写一个服务接口`MyService`，并为它添加实现类

   ```java
   import com.jyannis.servicea.message.BonusPointMessage;
   
   public interface MyService {
   
       void myService(BonusPointMessage bonusPointMessage);
   
   }
   ```

   这里的实体`BonusPointMessage`也就是我们4.1实现消息生产者中的实体，无需太过在意。

   实现类：

   ```java
   @Service
   @Slf4j
   public class MyServiceImpl implements MyService {
   
       @Autowired
       RocketMQTemplate rocketMQTemplate;
   
       @Override
       @Transactional(rollbackFor = Exception.class)
       public void myService(BonusPointMessage bonusPointMessage) {
   
           //做一些先行的业务处理（例如审核一下订单）
   
           //发送消息，准备让其他微服务来做后续的业务处理（例如为用户添加积分交给用户微服务来做）
           //发送半消息
           String transactionId = UUID.randomUUID().toString();
   
           rocketMQTemplate.sendMessageInTransaction(
                   "transaction-group",//生产者组
                   "bonus-point",//主题topic
                   MessageBuilder
                           .withPayload(bonusPointMessage)
                           // 也可以利用header存一些数据，例如事务id等
                           .setHeader(RocketMQHeaders.TRANSACTION_ID, transactionId)
                           .build(),
                   //arg是Object类型，可以完全根据我们的业务场景根据需求实现，非常自由
                   "这是我们的arg"
           );
   
           log.info("业务处理完成，半消息已发送");
       }
   }
   ```

   这里是举了这样一个例子：

   service-a处理一个用户下订单的逻辑，如果用户下订单成功，就为用户添加积分。而添加积分的业务由service-b来提供。

   所以在MyServiceImpl中，写了一个注释说“做一些先行的业务处理（例如审核一下订单）”。然后根据我们rocketmq实现分布式事务的逻辑，发送一个半消息。

   而关于半消息的发送，我们使用的是`rocketMQTemplate.sendMessageInTransaction`这个方法，具体如何调用看注释即可。

   最后打印一条日志表示半消息已发送。

   在运行逻辑上，在发送半消息成功后，程序会跳到事务监听接口的`executeLocalTransaction`方法，然后才会打印“业务处理完成，半消息已发送”的日志。

   事务监听接口是什么？请继续往下看。

   

3. 写一个事务监听接口

   这个接口主要用以执行本地事务，以及编写rocketmq进行回查时调用的回查逻辑。

   ```java
   @RocketMQTransactionListener(txProducerGroup = "transaction-group")
   @Slf4j
   public class MessageTransactionListener implements RocketMQLocalTransactionListener {
   
       /**
        * 执行本地事务
        * @param msg 也就是TestController中sendMessageInTransaction方法下的Message<?> message参数
        * @param arg 也就是TestController中sendMessageInTransaction方法下的MObject arg参数
        * @return 事务状态
        */
       @Override
       public RocketMQLocalTransactionState executeLocalTransaction(Message msg, Object arg) {
           MessageHeaders headers = msg.getHeaders();
   
           //可以从header里取信息
           log.info("TRANSACTION_ID = {}", headers.get(RocketMQHeaders.TRANSACTION_ID));
   
           //可以获取到arg
           log.info("arg = {}",arg);
   
           try {
               //把一个本地事务成功的日志存进数据库，便于rocketmq进行回查
               //这里为了减小代码复杂度，不调用服务进行数据库操作了，直接打印一个信息
               log.info("本地事务执行成功");
               return RocketMQLocalTransactionState.COMMIT;
           } catch (Exception e) {
               log.info("本地事务执行失败");
               return RocketMQLocalTransactionState.ROLLBACK;
           }
       }
   
       /**
        * rocketmq事务回查，以确定是要正常发送消息，还是将消息弃置
        * @param msg
        * @return 事务状态
        */
       @Override
       public RocketMQLocalTransactionState checkLocalTransaction(Message msg) {
           //检查本地事务是否执行成功了
           //这里就需要查出我们之前存进数据库的日志信息，以确定本地事务是否执行成功
           //这里为了减小代码复杂度，不调用服务进行数据库操作了，直接打印一个信息
           log.info("回查成功");
           return RocketMQLocalTransactionState.COMMIT;
       }
   }
   ```

   在`executeLocalTransaction`方法中，我们要返回本地事务状态`RocketMQLocalTransactionState`，来告诉rocketmq我们的本地事务是否执行成功。

   - 如果返回的是`RocketMQLocalTransactionState.COMMIT`，就说明本地事务执行成功了，rocketmq就会把service-a发送的消息真正投递给service-b。

   - 如果返回的是`RocketMQLocalTransactionState.ROLLBACK`，就说明本地事务执行失败，rocketmq就会把service-a发送的消息弃置，不会投递给service-b。

   - 如果没能正常返回，service-a宕机了。那么在一段时间后，rocketmq就会对service-a发起回查。如果这个时候service-a恢复运行了，就会调用`checkLocalTransaction`方法，重新获取本地事务状态。

<br/>

<br/>


# 测试

1. 启动*RocketMQ NameServer*和*RocketMQ Broker*

   如何启动请参考[启动RocketMQ](https://github.com/jyannis/Spring-Cloud-Alibaba-Learning/tree/master/4.RocketMQ#windows%E5%AE%89%E8%A3%85%E4%B8%8E%E5%90%AF%E5%8A%A8)

2. debug启动*service-a*，*service-b*

<br/>

## 本地事务成功的流程测试

1. 调用*service-a*下的*send*服务，随意传一个*BonusPointMessage*，控制台输出日志如下：

   注意：C盘要有足够的可用空间，不然可能生产消息失败

   ```java
   2020-05-10 14:43:54.749  INFO 13808 --- [nio-8181-exec-1] c.j.servicea.MessageTransactionListener  : TRANSACTION_ID = 04adf0ce-635d-4fc5-9fd0-0a6c0ba2bd66
   2020-05-10 14:43:57.270  INFO 13808 --- [nio-8181-exec-1] c.j.servicea.MessageTransactionListener  : arg = 这是我们的arg
   2020-05-10 14:43:57.270  INFO 13808 --- [nio-8181-exec-1] c.j.servicea.MessageTransactionListener  : 本地事务执行成功
   2020-05-10 14:43:57.276  INFO 13808 --- [nio-8181-exec-1] c.j.servicea.service.impl.MyServiceImpl  : 业务处理完成，半消息已发送xxxxxxxxxx 2020-05-10 14:43:54.749  INFO 13808 --- [nio-8181-exec-1] c.j.servicea.MessageTransactionListener  : TRANSACTION_ID = 04adf0ce-635d-4fc5-9fd0-0a6c0ba2bd662020-05-10 14:43:57.270  INFO 13808 --- [nio-8181-exec-1] c.j.servicea.MessageTransactionListener  : arg = 这是我们的arg2020-05-10 14:43:57.270  INFO 13808 --- [nio-8181-exec-1] c.j.servicea.MessageTransactionListener  : 本地事务执行成功2020-05-10 14:43:57.276  INFO 13808 --- [nio-8181-exec-1] c.j.servicea.service.impl.MyServiceImpl  : 业务处理完成，半消息已发送success
   ```

   借助日志打印的顺序，我们也可以回顾一下这个运行流程：

   1. 调用send接口，进入controller层
   2. 调用service层
   3. service发送半消息
   4. 进入事务监听类，调用`executeLocalTransaction`方法，返回COMMIT事务状态
   5. 回到service层，执行后续代码

2. 查看*service-b*的控制台，可以看到日志：

   ```java
   2020-05-10 14:43:57.285  INFO 25596 --- [MessageThread_6] c.j.s.rocketmq.BonusPointListener        : 收到的消息是BonusPointMessage(userId=1, point=1.0)
   ```

   消费消息成功。

<br/>

## 本地事务失败的流程测试

1. 修改`MessageTransactionListener`的代码，让`executeLocalTransaction`方法在执行时进入错误的流程：

   ```java
   @RocketMQTransactionListener(txProducerGroup = "transaction-group")
   @Slf4j
   public class MessageTransactionListener implements RocketMQLocalTransactionListener {
   
       /**
        * 执行本地事务
        * @param msg 也就是TestController中sendMessageInTransaction方法下的Message<?> message参数
        * @param arg 也就是TestController中sendMessageInTransaction方法下的MObject arg参数
        * @return 事务状态
        */
       @Override
       public RocketMQLocalTransactionState executeLocalTransaction(Message msg, Object arg) {
           MessageHeaders headers = msg.getHeaders();
   
           //可以从header里取信息
           log.info("TRANSACTION_ID = {}", headers.get(RocketMQHeaders.TRANSACTION_ID));
   
           //可以获取到arg
           log.info("arg = {}",arg);
   
           try {
               //把一个本地事务成功的日志存进数据库，便于rocketmq进行回查
               //这里为了减小代码复杂度，不调用服务进行数据库操作了，直接打印一个信息
               throw new RuntimeException("出错了");
   //            log.info("本地事务执行成功");
   //            return RocketMQLocalTransactionState.COMMIT;
           } catch (Exception e) {
               log.info("本地事务执行失败");
               return RocketMQLocalTransactionState.ROLLBACK;
           }
       }
   
       /**
        * rocketmq事务回查，以确定是要正常发送消息，还是将消息弃置
        * @param msg
        * @return 事务状态
        */
       @Override
       public RocketMQLocalTransactionState checkLocalTransaction(Message msg) {
           //检查本地事务是否执行成功了
           //这里就需要查出我们之前存进数据库的日志信息，以确定本地事务是否执行成功
           //这里为了减小代码复杂度，不调用服务进行数据库操作了，直接打印一个信息
           log.info("回查成功");
           return RocketMQLocalTransactionState.COMMIT;
       }
   }
   ```

   

2. 重新debug启动*service-a*

   

3. 调用*service-a*下的*send*服务，随意传一个*BonusPointMessage*，控制台输出日志如下：

   注意：C盘要有足够的可用空间，不然可能生产消息失败

   ```java
   2020-05-10 15:34:00.231  INFO 15576 --- [nio-8181-exec-1] c.j.servicea.MessageTransactionListener  : TRANSACTION_ID = 8e221470-2886-43cb-b77b-48cf71e49698
   2020-05-10 15:34:04.449  INFO 15576 --- [nio-8181-exec-1] c.j.servicea.MessageTransactionListener  : arg = 这是我们的arg
   2020-05-10 15:34:04.450  INFO 15576 --- [nio-8181-exec-1] c.j.servicea.MessageTransactionListener  : 本地事务执行失败
   2020-05-10 15:34:04.457  INFO 15576 --- [nio-8181-exec-1] c.j.servicea.service.impl.MyServiceImpl  : 业务处理完成，半消息已发送
   ```

   `executeLocalTransaction`返回了`RocketMQLocalTransactionState.ROLLBACK`。rocketmq会认为本地事务执行失败，也就会弃置消息。

   

4. 查看*service-b*的控制台，确实看不到消息消费的日志。

<br/>

## 宕机触发回查的流程测试

1. 在`MessageTransactionListener`的`executeLocalTransaction`方法的任意一个位置打个断点，让方法无法正常返回即可。

   

2. 重新debug启动`service-a`

   

3. 调用*service-a*下的*send*服务，随意传一个*BonusPointMessage*，程序会暂停在打断点的位置。

   然后强制终止`service-a`，模拟一个宕机的效果。

   > 请注意：intellij idea中的stop按钮（也就是一个红色的小方块）点击后是正常结束，不是像linux的kill -9那样强行终止，所以用这个方式不行
   >
   > 如果不知道怎么强行终止程序的话，可以连续点击两下红色小方块，就可以强行终止了。
   >
   > 在点击第一下红方块之后，它会变成骷髅头图标，再点一下，就是强行终止。
   >
   > 但是因为点第一下的时候程序还在继续执行，到你点第二下的时候可能程序已经正常走完了。所以如果使用这个方式，请在`executeLocalTransaction`方法中加个Thread.sleep让线程挂起一会儿，让你能够尽快地强行终止进程。

   > 如果不理解上面那个强行终止的方法，也可以手动打开windows的任务管理器，直接把service-a对应的jar给强制退出掉。

   

4. 重启`service-a`，静等一会儿，发现控制台会输出以下日志：

   ```java
   2020-05-10 15:52:41.285  INFO 1204 --- [pool-1-thread-1] c.j.servicea.MessageTransactionListener  : 回查成功
   ```

   说明rocketmq端（也就是broker）确实发起了回查请求，回调了回查方法`checkLocalTransaction`，并返回了`RocketMQLocalTransactionState.COMMIT`表明本地事务执行成功。rocketmq选择不将消息弃置，而是正确投递给*service-b*。

   

5. 查看*service-b*的控制台，可以看到日志：

   ```java
   2020-05-10 15:52:41.307  INFO 25596 --- [MessageThread_7] c.j.s.rocketmq.BonusPointListener        : 收到的消息是BonusPointMessage(userId=1, point=1.0)
   ```

   消费消息成功。

