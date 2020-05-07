package com.jyannis.servicea;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.jyannis.servicea.message.MyMessage;


/**
 * 三种消息发送模式：同步、异步、单向
 * @author jyannis
 */
@RestController
@Slf4j
public class SendTypeController {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    private static final String topic = "sendType";

    @GetMapping("/sendType")
    public String send(){

        /**
         * 同步发送消息，需要等待broker响应
         */
        rocketMQTemplate.syncSend(topic,new MyMessage("这是一条同步发送的消息"));

        /**
         * 异步发送消息，无需等待broker响应，由专门的回调函数来等待回调
         * onSuccess(SendResult sendResult)在消息发送成功时回调
         * onException(Throwable throwable)在消息发送失败时回调
         */
        rocketMQTemplate.asyncSend(topic,new MyMessage("这是一条异步发送的消息"), new SendCallback() {

            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("异步消息发送成功了，broker得到了正确响应");
                log.info("消息id是：{}",sendResult.getMsgId());
            }

            @Override
            public void onException(Throwable throwable) {
                log.warn("异步消息发送出错，错误为：{}",throwable);
            }
        });

        /**
         * 以单向模式发送消息，无需等待broker响应，也无需回调
         */
        rocketMQTemplate.sendOneWay(topic,new MyMessage("这是一条单向模式发送的消息"));


        //正常返回
        return "success";
    }

}
