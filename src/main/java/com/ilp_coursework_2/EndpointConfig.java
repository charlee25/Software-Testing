package com.ilp_coursework_2;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EndpointConfig {

    @Bean
    public String ilpEndpoint() {
        String endpoint = System.getenv("ILP_ENDPOINT");

        if (endpoint == null || endpoint.isEmpty()) {
            endpoint = "http://localhost:8081/";
        }

        return endpoint;
    }
}