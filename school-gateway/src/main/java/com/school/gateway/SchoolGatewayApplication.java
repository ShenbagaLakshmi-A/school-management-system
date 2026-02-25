package com.school.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;

@SpringBootApplication(exclude = { WebMvcAutoConfiguration.class })
@EnableDiscoveryClient
public class SchoolGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(SchoolGatewayApplication.class, args);
    }
}
