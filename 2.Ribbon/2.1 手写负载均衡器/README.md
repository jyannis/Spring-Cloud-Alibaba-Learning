# 目标

1. 自定义一个简易版负载均衡器
2. 使用自定义的负载均衡器，完成service-a对service-b的远程调用



# 前置知识

1. 对日志有所了解（本仓库中主要使用lombok slf4j）
2. 已学习完1.Nacos下的全部内容



# 流程

本节只涉及*service-a*下的编码，不涉及*service-b*的编码。（*service-b*沿用1.Nacos最后一个版本的代码即可）

1. 手写一个简单的负载均衡器

   这里的负载均衡器很简单，就是传入一个节点数组的*size*，随机返回一个节点的下标。

   ```java
   /**
    * 负载均衡器
    */
   public class LoadBalancer {
   
       /**
        * 在[0,size)中选择一个整数
        */
       public static int selectOneRandomly(int size){
           if(size == 0)return -1;
           return ThreadLocalRandom.current().nextInt(size);
       }
   
   }
   ```

2. 修改*RemotingController*，调用自定义负载均衡器来实现负载均衡

   修改的核心代码如下

   ```java
   List<ServiceInstance> instances = discoveryClient.getInstances("service-b");
   
           //discoveryClient.getInstances是不会返回null的，
           //如果找不到会返回new ArrayList<>()，所以不用判断instances为null
           if(instances.size() == 0)return "failed";
   
           //调用负载均衡器，确定请求的目标地址
           int targetIndex = LoadBalancer.selectOneRandomly(instances.size());
           String targetUrl = instances.get(targetIndex).getUri().toString() + "/test/{argue}";
   ```





# 测试

1. 启动Nacos

2. 启动service-a

3. 启动若干个service-b实例（设为不同端口即可）

4. 调用service-a下的remote服务，查看日志：

   

   

