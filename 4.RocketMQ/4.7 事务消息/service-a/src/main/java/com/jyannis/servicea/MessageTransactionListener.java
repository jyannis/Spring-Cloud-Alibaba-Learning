package com.jyannis.servicea;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;

@RocketMQTransactionListener(txProducerGroup = "transaction-group")
@Slf4j
public class MessageTransactionListener implements RocketMQLocalTransactionListener {

    /**
     * 执行本地事务
     * @param msg 也就是TestController中sendMessageInTransaction方法下的Message<?> message参数
     * @param arg 也就是TestController中sendMessageInTransaction方法下的MObject arg参数
     * @return 事务状态
     */
    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        MessageHeaders headers = msg.getHeaders();

        //可以从header里取信息
        log.info("TRANSACTION_ID = {}", headers.get(RocketMQHeaders.TRANSACTION_ID));

        //可以获取到arg
        log.info("arg = {}",arg);

        try {
            //把一个本地事务成功的日志存进数据库，便于rocketmq进行回查
            //这里为了减小代码复杂度，不调用服务进行数据库操作了，直接打印一个信息
//            Thread.sleep(1000);
//            throw new RuntimeException("出错了");
            log.info("本地事务执行成功");
            return RocketMQLocalTransactionState.COMMIT;
        } catch (Exception e) {
            log.info("本地事务执行失败");
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }

    /**
     * rocketmq事务回查，以确定是要正常发送消息，还是将消息弃置
     * @param msg
     * @return 事务状态
     */
    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message msg) {
        //检查本地事务是否执行成功了
        //这里就需要查出我们之前存进数据库的日志信息，以确定本地事务是否执行成功
        //这里为了减小代码复杂度，不调用服务进行数据库操作了，直接打印一个信息
        log.info("回查成功");
        return RocketMQLocalTransactionState.COMMIT;
    }
}
