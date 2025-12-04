package com.laipe.electricitybusiness.config;

import com.laipe.electricitybusiness.web.SnakeCaseModelAttributeArgumentResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public SnakeCaseModelAttributeArgumentResolver snakeCaseModelAttributeArgumentResolver() {
        return new SnakeCaseModelAttributeArgumentResolver();
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        // Add at the beginning to ensure it takes priority
        resolvers.addFirst(snakeCaseModelAttributeArgumentResolver());
    }
}

