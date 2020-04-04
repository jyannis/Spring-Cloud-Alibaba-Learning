package com.jyannis.servicea;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;


@RestController
public class RemotingController {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("remote")
    public String remote(){
        //调用service-b的服务
        //用http get请求，并且返回对象
        return restTemplate.getForObject(
                "http://service-b/test/{argue}",
                String.class,
                "argue from service-a"
        );
    }

}
