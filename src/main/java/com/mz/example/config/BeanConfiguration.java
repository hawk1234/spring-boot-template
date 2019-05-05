package com.mz.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class BeanConfiguration {

    @Bean()
    @Scope(value = "singleton")
    public ApplicationConfig applicationConfig(){
        return new ApplicationConfig();
    }
}
