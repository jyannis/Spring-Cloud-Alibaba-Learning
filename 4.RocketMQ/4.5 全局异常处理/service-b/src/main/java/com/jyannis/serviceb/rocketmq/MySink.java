package com.jyannis.serviceb.rocketmq;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.MessageChannel;

public interface MySink {

    String MY_INPUT = "myInput";

    @Input(MY_INPUT)
    MessageChannel input();

}
