package com.jyannis.servicea.feignclient;

import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;


/**
 * 注解@FeignClient指定该类负责对service-b服务的远程调用
 */
@FeignClient(name = "service-b",configuration = ServiceBFeignClient.MultipartSupportConfig.class)
public interface ServiceBFeignClient {

    /**
     * 上传文件
     * @param file
     * @return
     */
    @PostMapping(value = "/upload",
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE},
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public String upload(@PathVariable("file") MultipartFile file);

    class MultipartSupportConfig{
        @Bean
        public Encoder feignFormEncoder(){
            return new SpringFormEncoder();
        }
    }

}
