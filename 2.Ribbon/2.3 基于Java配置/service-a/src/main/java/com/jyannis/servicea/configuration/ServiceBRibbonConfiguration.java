package com.jyannis.servicea.configuration;

import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.context.annotation.Configuration;
import ribbonconfig.RibbonConfiguration;

/**
 * @RibbonClient指定该配置类是为哪个服务服务的
 */
@Configuration
@RibbonClient(name = "service-b",configuration = RibbonConfiguration.class)
public class ServiceBRibbonConfiguration {
}
