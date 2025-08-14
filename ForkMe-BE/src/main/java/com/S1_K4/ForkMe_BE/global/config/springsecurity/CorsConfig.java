package com.S1_K4.ForkMe_BE.global.config.springsecurity;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * @author : 김관중
 * @packageName : com.S1_K4.ForkMe_BE.global.config.springsecurity
 * @fileName : CorsConfig
 * @date : 2025-08-09
 * @description : 테스트
 */
@Configuration
public class CorsConfig {
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration c = new CorsConfiguration();
        c.setAllowedOrigins(List.of("http://forkme.site:3000", "http://127.0.0.1:3000","http://localhost:3000"));
        c.setAllowedMethods(List.of("GET", "POST", "OPTIONS")); // 최소한의 추가(GET)
        c.setAllowedHeaders(List.of(
                "Authorization","Content-Type", "X-Hub-Signature-256", "X-GitHub-Event", "X-GitHub-Delivery"
        ));
        c.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource s = new UrlBasedCorsConfigurationSource();
        s.registerCorsConfiguration("/webhooks/github/**", c);
        s.registerCorsConfiguration("/api/**", c);
        return s;
    }
}
