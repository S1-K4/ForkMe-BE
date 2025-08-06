package com.S1_K4.ForkMe_BE.global.config.jwt;

import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

/**
 * @author : 김종국
 * @packageName : com.S1_K4.ForkMe_BE.global.config.jwt
 * @fileName : JwtKeyConfig
 * @date : 2025-08-04
 * @description : Jwt Key 설정
 */

@Configuration
public class JwtKeyConfig {

    @Value("${jwt.secret}")
    private String secretKey;

    @Bean
    public SecretKey jwtSecretKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }
}