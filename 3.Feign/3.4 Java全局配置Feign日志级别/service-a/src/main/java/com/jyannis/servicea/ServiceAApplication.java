package com.jyannis.servicea;

import com.jyannis.servicea.configuration.GlobalFeignConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableFeignClients(defaultConfiguration = GlobalFeignConfiguration.class)
public class ServiceAApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceAApplication.class, args);
	}

}
