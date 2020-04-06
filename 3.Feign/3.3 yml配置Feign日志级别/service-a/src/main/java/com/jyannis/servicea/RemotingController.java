package com.jyannis.servicea;

import com.jyannis.servicea.feignclient.ServiceBFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class RemotingController {

    @Autowired
    private ServiceBFeignClient serviceBFeignClient;

    @GetMapping("remote")
    public String remote(){
        //调用service-b的服务
        //用http get请求，并且返回对象
        return serviceBFeignClient.bTest("argue from service-a");
    }

}
