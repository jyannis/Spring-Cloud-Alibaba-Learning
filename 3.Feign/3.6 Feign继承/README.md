# 目标

1. 写一个*feign-api*项目来构建契约
2. 使用*service-b*作为服务提供者，来实现契约
3. 使用*service-a*作为服务消费者，来消费契约对应的服务

而*service-a*和*service-b*都是通过继承/实现*feign-api*提供的契约接口来达成目标的，故我们称这个技术为**Feign继承**。






# 前置知识

## 一、理解Feign继承的价值

例如在之前的几节中，我们的*service-a*需要调用*service-b*的*test*服务：

- *service-a*中的代码：

  ```java
  @FeignClient(name = "service-b")
  public interface ServiceBFeignClient {
  
      @GetMapping("/test/{argue}")
      String bTest(@PathVariable("argue") String argue);
  
  }
  ```



- *service-b*中的代码：

  ```java
  @RestController
  @Slf4j
  public class TestController {
  
      @Autowired
      HttpServletRequest request;
  
      @GetMapping("test/{argue}")
      public String test(@PathVariable("argue") String argue){
          log.info("请求的uri是：" + request.getRequestURI());
          return "this is service-b, argue = " + argue;
      }
  
  }
  ```



我们发现这两段代码中，这一部分都是重复的：

```java
    @GetMapping("test/{argue}")
    public String test(@PathVariable("argue") String argue)
```

如果我们的*url*以及参数过于复杂，那么就会导致很多的冗余工作；

以及如果我们返回的不是*String*类型而是实体类型，那么*service-a*和*service-b*同时也要维护相应的实体类型，也会非常复杂。尤其当实体类型经常迭代的时候，*service-a*和*service-b*就要分别做同步更新，工作量很大。

利用**Feign继承**，我们可以完成上面这一段代码（url和方法及其参数）及实体类型的复用。





## 二、理解Feign继承的弊端

Feign的官网上是不建议用户使用**Feign继承**的，这是因为它有悖于Feign设计的解耦初衷。

Feign继承会导致以下三者之间出现耦合：

- 契约包（本例中的*feign-api*）
- 服务端（本例中的*service-b*，也即服务提供者）
- 客户端（本例中的*service-a*，也即服务消费者）





# 流程

## 一、设计feign-api项目

我们设计一个实体*User*和一个契约，然后部署到本地仓库。

1. *pom.xml*中添加*lombok*，并修改*maven*的配置，不然打包时会出现*BOOT-INF*文件夹导致依赖不可用

   ```xml
   	<dependencies>
   		<dependency>
   			<groupId>org.springframework.boot</groupId>
   			<artifactId>spring-boot-starter</artifactId>
   		</dependency>
   
   		<dependency>
   			<groupId>org.springframework.boot</groupId>
   			<artifactId>spring-boot-starter-web</artifactId>
   		</dependency>
   
   		<!--lombok插件-->
   		<!--主要提供slf4j日志，还有实体类简化开发注解@Data等等-->
   		<dependency>
   			<groupId>org.projectlombok</groupId>
   			<artifactId>lombok</artifactId>
   			<version>1.18.8</version>
   			<scope>provided</scope>
   		</dependency>
   	</dependencies>
   
   	<build>
   		<plugins>
   			<!--消除BOOT-INF-->
   			<plugin>
   				<groupId>org.springframework.boot</groupId>
   				<artifactId>spring-boot-maven-plugin</artifactId>
   				<configuration>
   					<skip>true</skip>
   				</configuration>
   			</plugin>
   		</plugins>
   	</build>
   ```

   

2. 实体类*User*

   ```java
   @Data
   @Builder
   public class User implements Serializable{
   
       private static final long serialVersionUID = -201215680909203539L;
   
       private String username;
       private String password;
   
   }
   ```

   

3. 接口*TestService*

   ```java
   public interface TestService {
   
       @GetMapping("/test/{username}")
       User test(@PathVariable("username")String username);
   
   }
   ```



1. 打包服务

   以下两者都可：

   1. 执行*mvn install*命令
   2. 在IDEA左上角点击*View* → *Tool Windows* → *Maven Projects* → *Lifecycle* → *install*





## 二、设计service-b实现服务

1. 在*pom.xml*中引入*feign-api*

   ```xml
   		<dependency>
   			<groupId>com.jyannis</groupId>
   			<artifactId>feign-api</artifactId>
   			<version>1.0.0</version>
   		</dependency>
   ```

   

2. 实现服务*TestService*

   这里随便写了点业务逻辑，就是传入*username*之后传出一个用户名密码都是*username*的实体*User*

   ```java
   @RestController
   @Slf4j
   public class TestController implements TestService{
   
   
       @Override
       public User test(String username) {
           return User.builder().username(username).password(username).build();
       }
   }
   ```





## 三、设计service-a消费服务

1. 在pom.xml中引入*feign-api*

   ```xml
   		<dependency>
   			<groupId>com.jyannis</groupId>
   			<artifactId>feign-api</artifactId>
   			<version>1.0.0</version>
   		</dependency>
   ```

   

2. 继承TestService获取服务，并交给FeignClient来代理

   ```java
   /**
    * 注解@FeignClient指定该类负责对service-b服务的远程调用
    */
   @FeignClient(name = "service-b")
   public interface ServiceBFeignClient extends TestService{
   
   }
   ```



至此，我们就完成了契约*feign-api*，服务提供者*service-b*，服务消费者*service-a*的设计及实现，我们可以发现代码得到了较大的简化。






# 测试

1. 启动*Nacos*

2. 启动*service-a*，*service-b*

4. 调用*service-a*下的*remote*服务，返回信息如下：


```json
{
  "username": "argue from service-a",
  "password": "argue from service-a"
}
```


