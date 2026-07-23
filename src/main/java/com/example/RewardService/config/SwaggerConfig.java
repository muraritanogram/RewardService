package com.example.RewardService.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi publicApi(){
    return GroupedOpenApi.builder().
            group("RewardService")
            .packagesToScan("com.example")
            .pathsToMatch("/**").build();
    }
}
