package com.jyannis.servicea;

import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.jyannis.servicea.message.BonusPointMessage;


@RestController
public class TestController {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @GetMapping("/send")
    public String send(BonusPointMessage bonusPointMessage){

        //处理业务

        //生产消息，由其他服务来完成用户积分加分的业务逻辑
        //我们把这个消息放在point这一topic下
        rocketMQTemplate.convertAndSend("point",bonusPointMessage);

        //正常返回
        return "success";
    }

}
