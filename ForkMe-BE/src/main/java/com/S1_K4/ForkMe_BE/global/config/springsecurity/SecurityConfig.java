package com.S1_K4.ForkMe_BE.global.config.springsecurity;

import com.S1_K4.ForkMe_BE.global.security.jwt.JwtTokenFilter;
import com.S1_K4.ForkMe_BE.modules.auth.OAuth2AuthenticationSuccessHandler;
import com.S1_K4.ForkMe_BE.modules.auth.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.global.config.springsecurity
 * @fileName : SecurityConfig
 * @date : 2025-08-03
 * @description : springSecurity 관련 설정파일입니다.
 */
@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler OAuth2AuthenticationSuccessHandler;
    private final JwtTokenFilter jwtTokenFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        // ====== Public ======
                        .requestMatchers(
                                "/", "/index.html","/login/**","/api/auth/**"
                        ).permitAll()

                        //인증 필요한 GET 매핑
                        .requestMatchers(HttpMethod.GET,
                                "/api/projects/form-info",
                                "/api/projects/*/update-form"
                        ).authenticated()

                        //인증 필요
                        .requestMatchers(HttpMethod.POST,   "/api/projects/**").authenticated()
                        .requestMatchers(HttpMethod.PUT,    "/api/projects/**").authenticated()
                        .requestMatchers(HttpMethod.PATCH,  "/api/projects/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/projects/**").authenticated()

                        //나머지 인증x Get매핑
                        .requestMatchers(HttpMethod.GET, "/api/projects/**").permitAll()

                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(auth -> auth
                                .baseUri("/login")
                        )
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        .successHandler(OAuth2AuthenticationSuccessHandler)
                )
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


}