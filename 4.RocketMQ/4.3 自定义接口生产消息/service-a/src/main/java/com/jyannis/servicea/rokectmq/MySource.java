package com.jyannis.servicea.rokectmq;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface MySource {

    @Output("myOutput")
    MessageChannel output();

}
