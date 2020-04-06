package com.jyannis.feignapi;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class User implements Serializable{

    private static final long serialVersionUID = -201215680909203539L;

    private String username;
    private String password;

}
