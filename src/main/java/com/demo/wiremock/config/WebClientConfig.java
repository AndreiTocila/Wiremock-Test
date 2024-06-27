package com.demo.wiremock.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig
{
    @Bean
    public WebClient toDoWebClient(@Value("${api.base.url}") String url, WebClient.Builder webClientBuilder)
    {
        return webClientBuilder
                .baseUrl(url)
                .build();
    }
}
