package com.S1_K4.ForkMe_BE.global.security.jwt;

import com.S1_K4.ForkMe_BE.modules.auth.service.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * @author : 김종국
 * @packageName : com.S1_K4.ForkMe_BE.global.security.jwt
 * @fileName : JwtTokenFilter
 * @date : 2025-08-04
 * @description : JWT 토큰 검사
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();

        return

                path.startsWith("/login/github")
                ||path.startsWith("/favicon.ico")
                ||path.startsWith("/api/projects");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        log.info("토큰 유효성 검사 : " + request.getRequestURI());
        String accessToken = resolveToken(request);
        log.info("accessToken : " + accessToken + " /");

//        // 토큰 유효성 검사
//        if (StringUtils.hasText(accessToken) && jwtTokenProvider.validateToken(accessToken)) {
//            Authentication authentication = jwtTokenProvider.getAuthenticationByToken(accessToken);
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//        }

        try {
            if (StringUtils.hasText(accessToken)) {
                // 🔥 여기서만 검증 & 인증 처리
                if (!jwtTokenProvider.validateToken(accessToken)) {
                    throw new BadCredentialsException("유효하지 않은 JWT 토큰입니다.");
                }

                Authentication authentication = jwtTokenProvider.getAuthenticationByToken(accessToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            SecurityContextHolder.clearContext();
            throw ex; // 반드시 예외를 던져야 SecurityConfig에서 401로 처리함
        }


        filterChain.doFilter(request, response);
    }

    // Request Header에서 Authorization 토큰 정보 꺼내는 메소드
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}