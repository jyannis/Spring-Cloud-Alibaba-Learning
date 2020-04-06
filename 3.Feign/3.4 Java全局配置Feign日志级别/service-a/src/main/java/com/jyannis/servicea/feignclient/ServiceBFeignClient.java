package com.jyannis.servicea.feignclient;

import com.jyannis.servicea.configuration.GlobalFeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 注解@FeignClient指定该类负责对service-b服务的远程调用
 */
@FeignClient(name = "service-b")
public interface ServiceBFeignClient {

    /**
     * 调用bTest方法时，
     * Feign会帮我们转换为请求http://service-b/test/{argue}
     * 再经由Ribbon解析service-b服务的地址
     * 最终请求到service-b的/test/{argue}接口
     * @param argue
     * @return
     */
    @GetMapping("/test/{argue}")
    String bTest(@PathVariable("argue") String argue);

}
