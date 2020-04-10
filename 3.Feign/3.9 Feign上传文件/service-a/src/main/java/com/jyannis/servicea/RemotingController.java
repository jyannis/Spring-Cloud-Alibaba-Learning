package com.jyannis.servicea;

import com.jyannis.servicea.feignclient.ServiceBFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
@Slf4j
public class RemotingController {

    @Autowired
    private ServiceBFeignClient serviceBFeignClient;

    @PostMapping("remote")
    public String remote(@RequestPart("file") MultipartFile file){
        return serviceBFeignClient.upload(file);
    }

}
