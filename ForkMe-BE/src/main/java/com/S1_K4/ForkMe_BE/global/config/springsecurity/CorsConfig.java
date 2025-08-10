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
        c.setAllowedOriginPatterns(List.of("*")); // file:// 로 테스트해도 통과
        c.setAllowedMethods(List.of("POST", "OPTIONS"));
        c.setAllowedHeaders(List.of(
                "Content-Type", "X-Hub-Signature-256", "X-GitHub-Event", "X-GitHub-Delivery"
        ));
        c.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource s = new UrlBasedCorsConfigurationSource();
        s.registerCorsConfiguration("/webhooks/github/**", c);
        return s;
    }
}
