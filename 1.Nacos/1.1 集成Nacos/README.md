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
      
      ![启动nacos](https://gitee.com/jyannis/doc/raw/master/Spring-Cloud-Alibaba-Learning/1.Nacos/%E5%90%AF%E5%8A%A8nacos.png)
      
      
   
2. 编写两个微服务service-a和service-b（建立两个springboot工程即可）

   1. 在pom.xml中添加对spring-cloud-alibaba的依赖

      ```
      	<dependencyManagement>
      		<dependencies>
      			<!--整合spring cloud-->
      			<dependency>
      				<groupId>org.springframework.cloud</groupId>
      				<artifactId>spring-cloud-dependencies</artifactId>
      				<version>Greenwich.SR1</version>
      				<type>pom</type>
      				<scope>import</scope>
      			</dependency>
      			<!--整合spring cloud alibaba-->
      			<dependency>
      				<groupId>org.springframework.cloud</groupId>
      				<artifactId>spring-cloud-alibaba-dependencies</artifactId>
      				<version>0.9.0.RELEASE</version>
      				<type>pom</type>
      				<scope>import</scope>
      			</dependency>
      		</dependencies>
      	</dependencyManagement>
      ```

      

   2. 在pom.xml中添加对nacos-discovery的依赖

      ```
      		<dependency>
      			<groupId>org.springframework.cloud</groupId>
      			<artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
      		</dependency>
      ```

   3. 在yml中配置nacos server的地址及微服务的名称

      ```
      spring:
        cloud:
          nacos:
            discovery:
              #指定nacos server的地址
              server-addr: localhost:8848
        application:
          #服务名称（尽量用-，不要用_，更不要用特殊字符）
          name: service-a
      ```

      

3. 启动服务service-a和service-b，会看到nacos注册成功字样

   ![nacos注册成功控制台信息](https://gitee.com/jyannis/doc/raw/master/Spring-Cloud-Alibaba-Learning/1.Nacos/nacos%E6%B3%A8%E5%86%8C%E6%88%90%E5%8A%9F%E6%8E%A7%E5%88%B6%E5%8F%B0%E4%BF%A1%E6%81%AF.png)

   

4. 访问localhost:8848/nacos，进入nacos控制台并登录（用户名和密码默认都是nacos）

   ![nacos控制台登录界面](https://gitee.com/jyannis/doc/raw/master/Spring-Cloud-Alibaba-Learning/1.Nacos/nacos%E6%8E%A7%E5%88%B6%E5%8F%B0%E7%99%BB%E5%BD%95%E7%95%8C%E9%9D%A2.png)

   

5. 查看服务列表，可以看到service-a和service-b注册成功

   ![nacos服务列表](https://gitee.com/jyannis/doc/raw/master/Spring-Cloud-Alibaba-Learning/1.Nacos/nacos%E6%9C%8D%E5%8A%A1%E5%88%97%E8%A1%A8.png)

   

