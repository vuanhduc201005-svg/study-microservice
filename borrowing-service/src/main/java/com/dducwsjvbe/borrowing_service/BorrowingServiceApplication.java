package com.dducwsjvbe.borrowing_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@EnableDiscoveryClient
@SpringBootApplication
@ComponentScan({"com.dducwsjvbe.borrowing_service", "com.dducwsjvbe.common_service"})
public class BorrowingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BorrowingServiceApplication.class, args);
    }

}
