//package com.dducwsjvbe.common_service.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.client.reactive.ReactorClientHttpConnector;
//import org.springframework.web.reactive.function.client.WebClient;
//import reactor.netty.http.client.HttpClient;
//
//import java.time.Duration;
//
//@Configuration
//public class WebClientConfig {
//    @Bean
//    public WebClient bookServiceClient(WebClient.Builder builder) {
//        return builder
//                .baseUrl("http://localhost:9001")
//                .clientConnector(new ReactorClientHttpConnector(
//                        HttpClient.create().responseTimeout(Duration.ofSeconds(5))))
//                .build();
//    }
//
//    @Bean
//    public WebClient employeeServiceClient(WebClient.Builder builder) {
//        return builder
//                .baseUrl("http://localhost:9002")
//                .clientConnector(new ReactorClientHttpConnector(
//                        HttpClient.create().responseTimeout(Duration.ofSeconds(5))))
//                .build();
//    }
//}
