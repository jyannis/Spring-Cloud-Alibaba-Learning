package com.jyannis.feignapi;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface TestService {

    @GetMapping("/test/{username}")
    User test(@PathVariable("username")String username);

}
