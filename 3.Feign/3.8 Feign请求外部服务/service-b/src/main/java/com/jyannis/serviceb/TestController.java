package com.jyannis.serviceb;

import org.springframework.web.bind.annotation.*;


@RestController
public class TestController {

    /**
     * GET path
     * 路径参数
     * @param argue
     * @return
     */
    @GetMapping("path/{argue}")
    public String path(@PathVariable("argue") String argue){
        return argue;
    }

}
