package com.jyannis.serviceb.rocketmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.ErrorMessage;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 全局异常处理
     * @param message
     */
    @StreamListener("errorChannel")
    public void handleError(Message<?> message){
        ErrorMessage errorMessage = (ErrorMessage) message;
        log.error("发现异常：{}",errorMessage);
    }

}
