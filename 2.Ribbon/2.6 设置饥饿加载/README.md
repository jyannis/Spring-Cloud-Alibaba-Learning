# 目标

1. 以*yml*配置*Ribbon*的方式，把*Ribbon*默认懒加载的方式修改为饥饿加载，提升第一次访问时的性能。





# 原理

​	*Ribbon*默认是懒加载的。

​	也就是说，对于下面这段代码：

```java
        //调用service-b的服务
        //用http get请求，并且返回对象
        return restTemplate.getForObject(
                "http://service-b/test/{argue}",
                String.class,
                "argue from service-a"
        );
```

​	在默认的懒加载下，第一次执行这段代码时，*Ribbon*还未初始化名为*service-b*的*Ribbon Client*，需要执行初始化才能继续执行代码。这就导致第一次访问*remote*接口时会很慢。

​	我们可以将*Ribbon*修改为饥饿加载的模式，以启动时间为代价换取第一次访问效率的提升。





# 流程

在本节中，只涉及对*service-a*编码的修改，不涉及对*service-b*的修改。

在*yml*配置文件中补充：

```yaml
ribbon:
  eager-load:
    # 指定饥饿加载
    enabled: true
    # 指定饥饿加载的服务名，多个服务名之间用英文逗号隔开
    clients: service-b
```





# 测试

这里就不测试了，有兴趣的读者可以自行添加对系统时间的打印，对比一下懒加载与饥饿加载下第一次访问的性能差别。


