package com.jyannis.serviceb.rocketmq;

import com.jyannis.serviceb.message.BonusPointMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

/**
 * 这里@RocketMQMessageListener(consumerGroup = "b-group1",topic = "point")
 * 表明接收的是主题为point的消息，且这个消费者隶属于b-group1消费者组
 */
@Service
@RocketMQMessageListener(consumerGroup = "b-group1",topic = "point")
@Slf4j
public class BonusPointListener implements RocketMQListener<BonusPointMessage>{

    /**
     * 收到消息时调用的方法
     * @param bonusPointMessage 收到的消息
     */
    @Override
    public void onMessage(BonusPointMessage bonusPointMessage) {
        log.info("收到的消息是{}",bonusPointMessage);
    }
}
