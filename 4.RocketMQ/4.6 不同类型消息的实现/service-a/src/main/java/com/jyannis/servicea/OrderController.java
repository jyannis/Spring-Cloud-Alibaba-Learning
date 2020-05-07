package com.jyannis.servicea;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 顺序发送消息
 * 这里的顺序是由MessageQueue保证的
 * 同一个MessageQueue下的消息是顺序的，不同MessageQueue的消息之间的顺序性是无法保证的
 * @author jyannis
 */
@RestController
@Slf4j
public class OrderController {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    private static final String topic = "order";

    @GetMapping("/order")
    public String send() {

        /**
         * 相同的hashKey，就相当于相同的MessageQueue
         * 相同的MessageQueue下消息是顺序的
         */
        rocketMQTemplate.syncSendOrderly(topic,"message1","order");
        rocketMQTemplate.syncSendOrderly(topic,"message2","order");
        rocketMQTemplate.syncSendOrderly(topic,"message3","order");
        rocketMQTemplate.syncSendOrderly(topic,"message4","order");

        return "success";
    }



}
