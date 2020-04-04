package com.jyannis.servicea.configuration;

import org.springframework.cloud.netflix.ribbon.RibbonClients;
import org.springframework.context.annotation.Configuration;
import ribbonconfig.RibbonConfiguration;

/**
 * RibbonClients指定该配置类是为全局服务的
 */
@Configuration
@RibbonClients(defaultConfiguration = RibbonConfiguration.class)
public class ServiceBRibbonConfiguration {
}
