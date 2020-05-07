package com.jyannis.serviceb.rocketmq.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.annotation.SelectorType;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

/**
 * TAG模式过滤示例
 * @author jyannis
 */
@Service
@RocketMQMessageListener(consumerGroup = "tag-filter-group",topic = "tagFilter"
        ,selectorType = SelectorType.TAG,selectorExpression = "TAGFILTER_ALL || TAGFILTER_A")
@Slf4j
public class TagFilterListener implements RocketMQListener<String> {


    @Override
    public void onMessage(String s) {
        log.info("收到消息：message = {}",s);
    }
}
