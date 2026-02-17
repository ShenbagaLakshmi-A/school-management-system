package com.hotel.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;

@SpringBootApplication(exclude = { WebMvcAutoConfiguration.class })
@EnableDiscoveryClient
public class HotelGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(HotelGatewayApplication.class, args);
    }
}
