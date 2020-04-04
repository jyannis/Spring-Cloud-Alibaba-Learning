package com.jyannis.servicea;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TestController {

    //由SpringCloud提供的组件，和Nacos解耦
    //也就是说 如果我们不使用Nacos而是其他的服务发现组件，依然可以使用DiscoveryClient
    @Autowired
    private DiscoveryClient discoveryClient;

    /**
     * 测试服务发现
     * 获取服务名为service-b的所有服务节点实例
     * @return serviceInstanceList
     */
    @GetMapping("testInstances")
    public List<ServiceInstance> getInstances(){
         return discoveryClient.getInstances("service-b");
    }

    /**
     * 测试服务发现
     * 获取服务名列表
     * @return serviceNamesList
     */
    @GetMapping("testServices")
    public List<String> getServiceNames(){
        return discoveryClient.getServices();
    }

}
