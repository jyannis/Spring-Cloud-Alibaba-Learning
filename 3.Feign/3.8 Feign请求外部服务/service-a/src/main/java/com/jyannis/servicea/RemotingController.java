package com.jyannis.servicea;

import com.jyannis.servicea.feignclient.ExternalFeignClient;
import com.jyannis.servicea.feignclient.ServiceBFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Slf4j
public class RemotingController {

    @Autowired
    private ServiceBFeignClient serviceBFeignClient;

    @Autowired
    private ExternalFeignClient externalFeignClient;

    @GetMapping("remote1")
    public String remote1(){
        log.info(serviceBFeignClient.path("GET path argue"));
        return "success";
    }

    @GetMapping("remote2")
    public String remote2(){
        log.info(externalFeignClient.path("GET path argue"));
        return "success";
    }

}
