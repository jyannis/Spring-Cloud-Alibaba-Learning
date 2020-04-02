package com.jyannis.serviceb;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("test/{argue}")
    public String test(@PathVariable("argue") String argue){
        return "this is service-b, argue = " + argue;
    }

}
