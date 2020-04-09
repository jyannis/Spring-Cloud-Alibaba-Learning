package com.jyannis.servicea.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 即使是调用服务发现组件外的服务，这个FeignClient也要指定name或者value参数
 * url参数指向的是这个FeignClient要调用的服务地址，一般是{ip}:{port}
 */
@FeignClient(name = "service-b-external",url = "localhost:8183")
public interface ExternalFeignClient {

    /**
     * GET path
     * 路径参数
     * @param argue
     * @return
     */
    @GetMapping("path/{argue}")
    public String path(@PathVariable("argue") String argue);

}
