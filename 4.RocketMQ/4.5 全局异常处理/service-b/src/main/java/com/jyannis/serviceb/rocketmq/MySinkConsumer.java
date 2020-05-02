package com.jyannis.serviceb.rocketmq;

import com.jyannis.serviceb.message.BonusPointMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MySinkConsumer {

    @StreamListener(MySink.MY_INPUT)
    public void receive(BonusPointMessage bonusPointMessage){
        log.info("收到消息：message={}", bonusPointMessage);
        throw new RuntimeException("运行时异常");
    }

}
