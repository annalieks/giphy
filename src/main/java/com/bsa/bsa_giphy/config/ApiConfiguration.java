package com.bsa.bsa_giphy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.net.http.HttpClient;

@Configuration
public class ApiConfiguration {
    @Bean
    @Scope(value = "prototype")
    public HttpClient httpClient() {
        return HttpClient.newHttpClient();
    }
}
