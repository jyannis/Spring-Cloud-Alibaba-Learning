package com.jyannis.servicea;

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

    @GetMapping("remote")
    public String remote(){

        User user = User.builder().username("Jack").password("asd").build();
        log.info(serviceBFeignClient.path("GET path argue"));
        log.info(serviceBFeignClient.query("GET query argue"));
        log.info("GET form-data: {}",serviceBFeignClient.formdata(user));

        log.info(serviceBFeignClient.pathPost("POST path argue"));
        log.info(serviceBFeignClient.queryPost("POST query argue"));
        log.info("POST body: {}",serviceBFeignClient.bodyPost(user));
        log.info("POST form-data: {}",serviceBFeignClient.formdataPost(user));
        return "success";
    }

}
