package com.jyannis.servicea.configuration;

import feign.Logger;
import org.springframework.context.annotation.Bean;

/**
 * ServiceBFeignClient的Feign配置类
 */
public class ServiceBFeignConfiguration {

    @Bean
    public Logger.Level level(){
        /**
         * Feign支持四种日志级别
         * 请参考README.md
         */
        return Logger.Level.FULL;
    }

}
