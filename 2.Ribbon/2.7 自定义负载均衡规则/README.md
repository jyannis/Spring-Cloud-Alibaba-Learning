# 目标

1. 以继承*AbstractLoadBalancerRule*的方式，实现自己的负载均衡算法
2. 设计的负载均衡算法能够利用*Nacos*中设置的权重来均衡负载





# 准备工作

在*Nacos*控制台为两个*service-b*实例设置权重。

1. 启动*Nacos*

2. 启动两个*service-b*实例

3. 输入[localhost:8848/nacos]()登录*Nacos*控制台（用户名密码均默认为*nacos*）

4. 点击服务列表-详情

   ![服务列表-详情](https://raw.githubusercontent.com/jyannis/SpringCloud-Alibaba-Learning/master/2.Ribbon/2.7%20%E8%87%AA%E5%AE%9A%E4%B9%89%E8%B4%9F%E8%BD%BD%E5%9D%87%E8%A1%A1%E8%A7%84%E5%88%99/docs/%E6%9C%8D%E5%8A%A1%E5%88%97%E8%A1%A8-%E8%AF%A6%E6%83%85.png)

5. 编辑两个实例的权重（权重值应在0-1之间）

   1. 把8182端口的权重设为0.2
   2. 把8183端口的权重设为0.5

   ![nacos-修改权重](https://raw.githubusercontent.com/jyannis/SpringCloud-Alibaba-Learning/master/2.Ribbon/2.7%20%E8%87%AA%E5%AE%9A%E4%B9%89%E8%B4%9F%E8%BD%BD%E5%9D%87%E8%A1%A1%E8%A7%84%E5%88%99/docs/nacos-%E4%BF%AE%E6%94%B9%E6%9D%83%E9%87%8D.png)





# 流程

在本节中，只涉及对*service-a*编码的修改，不涉及对*service-b*的修改。

1. 添加*MyRibbonRule*实现自定义的负载均衡算法

   1. 继承*AbstractLoadBalancerRule*需要实现两个方法

      1. *initWithNiwsConfig*这个方法一般不需要实现。事实上大多*Ribbon*内置提供的负载均衡算法也都是不实现它的，直接留空。

      2. *choose*方法是负载均衡的核心算法

         在本例中，代码流程如下：

         1. 获取*baseLoadBalancer*，*Ribbon*能提供给我们的大部分信息都在这里，我们通过它获取到我们要调用的服务名；
         2. 调用*Nacos*相关的组件和API，利用*Nacos*提供给我们的基于权重的负载均衡算法，得到最终要调用的实例；
         3. 通过`new NacosServer(instance)`把实例返回为Server类型。

   ```java
   /**
    * 继承IRule接口可以实现自定义的负载均衡算法
    * 继承AbstractLoadBalancerRule抽象类可以利用到ribbon帮我们封装好的一些内容
    */
   
   @Slf4j
   public class MyRibbonRule extends AbstractLoadBalancerRule {
   
       @Autowired
       private NacosDiscoveryProperties nacosDiscoveryProperties;
   
       @Override
       public void initWithNiwsConfig(IClientConfig iClientConfig) {
           //读取配置文件，并初始化NacosWeightedRule
       }
   
       @Override
       public Server choose(Object o) {
           try {
               ILoadBalancer loadBalancer = this.getLoadBalancer();
   
               //BaseLoadBalancer是Ribbon负载均衡器的基础实现类（非抽象类），
               //在该类中定义了很多关于负载均衡器相关的基础内容，对所有接口方法提供了实现。
               BaseLoadBalancer baseLoadBalancer = (BaseLoadBalancer)loadBalancer;
   
               //想要请求的微服务的名称
               String name = baseLoadBalancer.getName();
   
               //借助NamingService，我们可以使用许多服务发现的相关api
               NamingService namingService = nacosDiscoveryProperties.namingServiceInstance();
   
               //nacos client自动通过基于权重的负载均衡算法selectOneHealthyInstance，给我们选择一个实例
               Instance instance = namingService.selectOneHealthyInstance(name);
   
               log.info("选择的实例的端口 = {}",instance.getPort());
               return new NacosServer(instance);
           } catch (NacosException e) {
               e.printStackTrace();
           }
           return null;
       }
   }
   ```

   

2. 在*yml*配置文件中，为*service-b*服务添加*Ribbon*规则（回顾[2.4 基于yml配置](https://github.com/jyannis/SpringCloud-Alibaba-Learning/tree/master/2.Ribbon/2.4%20%E5%9F%BA%E4%BA%8Eyml%E9%85%8D%E7%BD%AE)）

```yaml
# 配置Ribbon规则
service-b:
  ribbon:
    NFLoadBalancerRuleClassName: com.jyannis.servicea.configuration.MyRibbonRule
```





# 测试

1. 启动Nacos

2. 启动*service-a*

3. 启动若干个*service-b*实例（设为不同端口即可）

4. 多次调用*service-a*下的*remote*服务，查看不同*service-b*节点的控制台日志输出：

   本例中我调用了九次*remote*服务，*service-a*的控制台日志如下：

```
2020-04-04 20:13:01.529  INFO 15232 --- [nio-8181-exec-8] c.j.servicea.configuration.MyRibbonRule  : 选择的实例的端口 = 8183
2020-04-04 20:13:02.108  INFO 15232 --- [io-8181-exec-10] c.j.servicea.configuration.MyRibbonRule  : 选择的实例的端口 = 8182
2020-04-04 20:13:02.656  INFO 15232 --- [nio-8181-exec-4] c.j.servicea.configuration.MyRibbonRule  : 选择的实例的端口 = 8183
2020-04-04 20:13:03.134  INFO 15232 --- [nio-8181-exec-2] c.j.servicea.configuration.MyRibbonRule  : 选择的实例的端口 = 8182
2020-04-04 20:13:03.603  INFO 15232 --- [nio-8181-exec-5] c.j.servicea.configuration.MyRibbonRule  : 选择的实例的端口 = 8183
2020-04-04 20:13:04.057  INFO 15232 --- [nio-8181-exec-8] c.j.servicea.configuration.MyRibbonRule  : 选择的实例的端口 = 8183
2020-04-04 20:13:04.494  INFO 15232 --- [io-8181-exec-10] c.j.servicea.configuration.MyRibbonRule  : 选择的实例的端口 = 8182
2020-04-04 20:13:04.949  INFO 15232 --- [nio-8181-exec-4] c.j.servicea.configuration.MyRibbonRule  : 选择的实例的端口 = 8183
2020-04-04 20:13:05.360  INFO 15232 --- [nio-8181-exec-2] c.j.servicea.configuration.MyRibbonRule  : 选择的实例的端口 = 8183
```

其中共调用8182节点的服务三次，8183节点的服务6次，基本符合0.2:0.5的权重比。
