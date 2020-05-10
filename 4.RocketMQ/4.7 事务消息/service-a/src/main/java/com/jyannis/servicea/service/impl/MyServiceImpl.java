package com.jyannis.servicea.service.impl;

import com.jyannis.servicea.message.BonusPointMessage;
import com.jyannis.servicea.service.MyService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
public class MyServiceImpl implements MyService {

    @Autowired
    RocketMQTemplate rocketMQTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void myService(BonusPointMessage bonusPointMessage) {

        //做一些先行的业务处理（例如审核一下订单）

        //发送消息，准备让其他微服务来做后续的业务处理（例如为用户添加积分交给用户微服务来做）
        //发送半消息
        String transactionId = UUID.randomUUID().toString();

        rocketMQTemplate.sendMessageInTransaction(
                "transaction-group",//生产者组
                "bonus-point",//主题topic
                MessageBuilder
                        .withPayload(bonusPointMessage)
                        // 也可以利用header存一些数据，例如事务id等
                        .setHeader(RocketMQHeaders.TRANSACTION_ID, transactionId)
                        .build(),
                //arg是Object类型，可以完全根据我们的业务场景根据需求实现，非常自由
                "这是我们的arg"
        );

        log.info("业务处理完成，半消息已发送");
    }
}
