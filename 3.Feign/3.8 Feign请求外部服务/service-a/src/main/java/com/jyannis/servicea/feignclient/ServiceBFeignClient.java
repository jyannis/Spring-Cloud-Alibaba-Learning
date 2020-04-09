package com.jyannis.servicea.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 注解@FeignClient指定该类负责对service-b服务的远程调用
 */
@FeignClient(name = "service-b")
public interface ServiceBFeignClient {

    /**
     * GET path
     * 路径参数
     * @param argue
     * @return
     */
    @GetMapping("path/{argue}")
    public String path(@PathVariable("argue") String argue);

}
