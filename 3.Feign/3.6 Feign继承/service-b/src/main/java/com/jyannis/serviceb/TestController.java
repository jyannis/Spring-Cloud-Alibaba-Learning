package com.jyannis.serviceb;

import com.jyannis.feignapi.TestService;
import com.jyannis.feignapi.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Slf4j
public class TestController implements TestService{


    @Override
    public User test(String username) {
        return User.builder().username(username).password(username).build();
    }
}
