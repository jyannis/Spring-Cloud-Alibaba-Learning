package com.jyannis.servicea;

import java.util.concurrent.ThreadLocalRandom;

/**
 * 负载均衡器
 */
public class LoadBalancer {

    /**
     * 在[0,size)中选择一个整数
     */
    public static int selectOneRandomly(int size){
        if(size == 0)return -1;
        return ThreadLocalRandom.current().nextInt(size);
    }

}
