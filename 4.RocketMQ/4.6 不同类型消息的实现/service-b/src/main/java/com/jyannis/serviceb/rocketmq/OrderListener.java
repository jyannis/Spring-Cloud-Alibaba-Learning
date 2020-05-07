package com.jyannis.serviceb.rocketmq;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.annotation.SelectorType;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

/**
 * 顺序消息的消费者
 * 指定consumeMode消费模式为“顺序的”
 * @author jyannis
 */
@Service
@RocketMQMessageListener(consumerGroup = "order-group",topic = "order",consumeMode = ConsumeMode.ORDERLY)
@Slf4j
public class OrderListener implements RocketMQListener<String>{

    @Override
    public void onMessage(String s) {
        log.info("顺序消费消息：message = {}",s);
    }
}
