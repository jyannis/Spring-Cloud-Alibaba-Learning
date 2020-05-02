package com.jyannis.servicea;

import com.jyannis.servicea.rokectmq.MySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.jyannis.servicea.message.BonusPointMessage;


@RestController
public class TestController {

    @Autowired
    private MySource mySource;

    @GetMapping("/send")
    public String send(BonusPointMessage bonusPointMessage){

        //处理业务

        //生产消息，由其他服务来完成用户积分加分的业务逻辑
        mySource.output()
                .send(
                        MessageBuilder
                                .withPayload(bonusPointMessage)
                        .build()
        );

        //正常返回
        return "success";
    }

}
