package com.jyannis.serviceb;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@Slf4j
public class TestController {

    @Autowired
    HttpServletRequest request;

    @GetMapping("test/{argue}")
    public String test(@PathVariable("argue") String argue){
        log.info("请求的uri是：" + request.getRequestURI());
        return "this is service-b, argue = " + argue;
    }

}
