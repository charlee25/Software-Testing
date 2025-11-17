package com.ilp_coursework_2;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EndpointConfig {

    @Bean
    public String ilpEndpoint() {
        String endpoint = System.getenv("ILP_ENDPOINT");

        if (endpoint == null || endpoint.isEmpty()) {
            endpoint = "https://ilp-rest-2025-bvh6e9hschfagrgy.ukwest-01.azurewebsites.net/";
        }

        return endpoint;
    }
}