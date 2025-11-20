package com.laipe.electricitybusiness.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
@RequiredArgsConstructor
public class RoutesConfig {
    @Bean
    public RequestMatcher publicRoutes() {
        return new OrRequestMatcher(
                PathPatternRequestMatcher.withDefaults().matcher("/api/auth/register"),
                PathPatternRequestMatcher.withDefaults().matcher("/api/auth/login"),
                PathPatternRequestMatcher.withDefaults().matcher("/h2-console/**"),
                PathPatternRequestMatcher.withDefaults().matcher("/api/stations/free"),
                PathPatternRequestMatcher.withDefaults().matcher("/api/stations/nearby"),
                PathPatternRequestMatcher.withDefaults().matcher("/api/stations/nearby-and-free"),
                PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.GET, "/api/stations/{id}")
        );
    }

    @Bean
    public RequestMatcher authorizedRoutesForBannedUsers() {
        return new OrRequestMatcher(
                PathPatternRequestMatcher.withDefaults().matcher("/api/auth/logout"),
                PathPatternRequestMatcher.withDefaults().matcher("/api/auth/status")
        );
    }

    @Bean
    public RequestMatcher authorizedRoutesForUnverifiedUsers() {
        return new OrRequestMatcher(
                PathPatternRequestMatcher.withDefaults().matcher("/api/auth/verify"),
                PathPatternRequestMatcher.withDefaults().matcher("/api/auth/refresh-verification-code"),
                PathPatternRequestMatcher.withDefaults().matcher("/api/auth/logout"),
                PathPatternRequestMatcher.withDefaults().matcher("/api/auth/status")
        );
    }
}
