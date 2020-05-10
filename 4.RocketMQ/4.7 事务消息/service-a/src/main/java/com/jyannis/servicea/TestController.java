package com.jyannis.servicea;

import com.jyannis.servicea.service.MyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.jyannis.servicea.message.BonusPointMessage;

import java.util.UUID;


@RestController
public class TestController {

    @Autowired
    private MyService myService;

    @GetMapping("/send")
    public String send(BonusPointMessage bonusPointMessage){

        myService.myService(bonusPointMessage);
        //正常返回
        return "success";
    }

}
