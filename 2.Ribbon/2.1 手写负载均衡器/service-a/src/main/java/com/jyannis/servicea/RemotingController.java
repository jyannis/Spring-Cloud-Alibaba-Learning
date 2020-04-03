package com.jyannis.servicea;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@Slf4j
public class RemotingController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DiscoveryClient discoveryClient;

    @GetMapping("remote")
    public String remote(){
        List<ServiceInstance> instances = discoveryClient.getInstances("service-b");

        //discoveryClient.getInstances是不会返回null的，如果找不到会返回new ArrayList<>()，所以不用判断instances为null
        if(instances.size() == 0)return "failed";

        //调用负载均衡器，确定请求的目标地址
        int targetIndex = LoadBalancer.selectOneRandomly(instances.size());
        String targetUrl = instances.get(targetIndex).getUri().toString() + "/test/{argue}";

        log.info("请求的目标地址：{}",targetUrl);

        //调用service-b的服务
        //用http get请求，并且返回对象
        return restTemplate.getForObject(
                targetUrl,
                String.class,
                "argue from service-a"
        );

    }

}
