package com.jyannis.servicea.feignclient;

import com.jyannis.feignapi.TestService;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * 注解@FeignClient指定该类负责对service-b服务的远程调用
 */
@FeignClient(name = "service-b")
public interface ServiceBFeignClient extends TestService{

}
