package com.jyannis.servicea.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * TAG模式过滤示例（请配合消费者示例查看，参考service-b项目下TagFilterListener）
 * @author jyannis
 */
@RestController
@Slf4j
public class TagFilterController {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @GetMapping("/tagFilter")
    public String send() {
        String topic = "tagFilter";

        rocketMQTemplate.syncSend(topic + ":TAGFILTER_ALL",new Message(topic, "TAGFILTER_ALL",  "基础数据1".getBytes()));
        rocketMQTemplate.syncSend(topic + ":TAGFILTER_ALL",new Message(topic, "TAGFILTER_ALL",  "基础数据2".getBytes()));
        rocketMQTemplate.syncSend(topic + ":TAGFILTER_A",new Message(topic, "TAGFILTER_A",  "A类数据".getBytes()));
        rocketMQTemplate.syncSend(topic + ":TAGFILTER_B",new Message(topic, "TAGFILTER_B",  "B类数据".getBytes()));

        return "success";
    }

}
