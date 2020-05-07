package com.jyannis.servicea;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 批量发送消息
 * 总量不能超过1MB，否则需要分片发送
 * @author jyannis
 */
@RestController
@Slf4j
public class BatchController {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @GetMapping("/batch")
    public String send() {
        String topic = "batch";
        List<Message> messages = new ArrayList<>();
        messages.add(new Message(topic, "TagA",  "Hello world 0".getBytes()));
        messages.add(new Message(topic, "TagA",  "Hello world 1".getBytes()));
        messages.add(new Message(topic, "TagA",  "Hello world 2".getBytes()));

        //同步发送消息
        rocketMQTemplate.syncSend(topic,messages);

        //异步发送、单向发送请参考SendTypeController.java

        return "success";
    }

}
