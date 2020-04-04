package com.jyannis.servicea.configuration;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.BaseLoadBalancer;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.alibaba.nacos.NacosDiscoveryProperties;
import org.springframework.cloud.alibaba.nacos.ribbon.NacosServer;


/**
 * 继承IRule接口可以实现自定义的负载均衡算法
 * 继承AbstractLoadBalancerRule抽象类可以利用到ribbon帮我们封装好的一些内容
 */

@Slf4j
public class MyRibbonRule extends AbstractLoadBalancerRule {

    @Autowired
    private NacosDiscoveryProperties nacosDiscoveryProperties;

    @Override
    public void initWithNiwsConfig(IClientConfig iClientConfig) {
        //读取配置文件，并初始化NacosWeightedRule
    }

    @Override
    public Server choose(Object o) {
        try {
            ILoadBalancer loadBalancer = this.getLoadBalancer();

            //BaseLoadBalancer是Ribbon负载均衡器的基础实现类（非抽象类），
            //在该类中定义了很多关于负载均衡器相关的基础内容，对所有接口方法提供了实现。
            BaseLoadBalancer baseLoadBalancer = (BaseLoadBalancer)loadBalancer;

            //想要请求的微服务的名称
            String name = baseLoadBalancer.getName();

            //借助NamingService，我们可以使用许多服务发现的相关api
            NamingService namingService = nacosDiscoveryProperties.namingServiceInstance();

            //nacos client自动通过基于权重的负载均衡算法selectOneHealthyInstance，给我们选择一个实例
            Instance instance = namingService.selectOneHealthyInstance(name);

            log.info("选择的实例的端口 = {}",instance.getPort());
            return new NacosServer(instance);
        } catch (NacosException e) {
            e.printStackTrace();
        }
        return null;
    }
}
