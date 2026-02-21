package com.hotel.monitoring;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAdminServer
public class MonitoringDashboardApplication {
    public static void main(String[] args) {
        SpringApplication.run(MonitoringDashboardApplication.class, args);
    }
}