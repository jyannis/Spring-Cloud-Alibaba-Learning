# 目标

1. 以*yml*配置*Ribbon*的方式，把*service-a*调用*service-b*时调用的负载均衡算法修改为*RandomRule*（随机选择节点）





# 前置知识

1. 了解Ribbon提供的几种负载均衡算法

   | 规则名称                  | 特点                                                         |
   | ------------------------- | ------------------------------------------------------------ |
   | AvailabilityFilteringRule | 过滤掉那些一直连接失败的被标记为circuit tripped的后端Server，并过滤掉那些高并发的的后端Server或者使用一个AvailabilityPredicate来包含过滤server的逻辑，其实就是检查status里记录的各个Server的运行状态。 |
   | BestAvailableRule         | 选择一个最小的并发请求的Server，逐个考察Server，如果Server被tripped了，则跳过。 |
   | RandomRule                | 随机选择一个Server。                                         |
   | RetryRule                 | 在选定的负载均衡策略subRule的基础上添加重试机制，在一个配置时间段内当选择Server不成功，则一直尝试使用subRule的方式选择一个可用的server。 |
   | RoundRobinRule            | 轮询选择， 轮询index，选择index对应位置的Server。            |
   | WeightedResponseTimeRule  | 根据响应时间分配一个weight(权重)，响应时间越长，weight越小，被选中的可能性越低。 |
   | ZoneAvoidanceRule         | **默认选用的规则**。复合判断Server所在区域的性能和Server的可用性选择Server。在没有Zone的环境下，类似于RoundRobinRule。 |

   



# 流程

在本节中，只涉及对*service-a*编码的修改，不涉及对*service-b*的修改。

1. 删除上节中对*Ribbon*的*java*代码配置（去除未打√的部分）

   - src
     - main
       - java
         - [x] com.jyannis.servicea
           - [ ] configuration
             - [ ] ServiceBRibbonConfiguration
           - [x] RemotingController
           - [x] ServiceAApplicaiton
           - [x] TestController
         - [ ] ribbonconfig
           - [ ] RibbonConfiguration

   

2. 在yml中添加针对*service-b*的*Ribbon*配置属性

   *NFLoadBalancerRuleClassName*也就是想应用的负载均衡算法的类的全限定名

   ```yaml
   # 配置Ribbon规则
   service-b:
     ribbon:
       NFLoadBalancerRuleClassName: com.netflix.loadbalancer.RandomRule
   ```

   




# 测试

1. 启动Nacos

2. 启动*service-a*

3. 启动若干个*service-b*实例（设为不同端口即可）

4. 多次调用*service-a*下的*remote*服务，查看不同*service-b*节点的控制台日志输出：

   本例中我调用了四次*remote*服务，两个service-b节点的控制台日志分别如下：

   ```
   2020-04-04 14:30:40.071  INFO 23772 --- [nio-8182-exec-1] com.jyannis.serviceb.TestController      : 请求的uri是：/test/argue%20from%20service-a
   ```
   
   ```
   2020-04-04 14:30:40.071  INFO 11340 --- [nio-8183-exec-1] com.jyannis.serviceb.TestController      : 请求的uri是：/test/argue%20from%20service-a
   2020-04-04 14:30:41.827  INFO 11340 --- [nio-8183-exec-2] com.jyannis.serviceb.TestController      : 请求的uri是：/test/argue%20from%20service-a
   2020-04-04 14:30:42.857  INFO 11340 --- [nio-8183-exec-3] com.jyannis.serviceb.TestController      : 请求的uri是：/test/argue%20from%20service-a
   ```
   
   其中端口为8182的节点接收了一次请求，端口为8183的节点接收了三次请求。说明我们成功替换了*Ribbon*的负载均衡算法，从默认的*ZoneAvoidanceRule*替换为了*RandomRule*。

