package com.S1_K4.ForkMe_BE.global.common.redis;

import com.S1_K4.ForkMe_BE.modules.auth.dto.CustomUserDetails;
import com.S1_K4.ForkMe_BE.modules.auth.service.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.List;
import java.util.Map;

/**
 * @author : 김남이
 * @packageName : com.S1_K4.ForkMe_BE.global.common.redis
 * @fileName : JwtHandshakeInterceptor
 * @date : 2025-08-14
 * @description : 웹소켓에서 jwt 인증을 추가하기 위해서 사용합니다.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes
    ) throws Exception {
        if (request instanceof ServletServerHttpRequest servletRequest) {
            HttpServletRequest httpServletRequest = servletRequest.getServletRequest();
            String token = httpServletRequest.getParameter("accessToken");
            if (token == null) {
                token = httpServletRequest.getHeader("Authorization");
                if (token != null && token.startsWith("Bearer ")) {
                    token = token.substring(7);
                }
            }
            if (token != null && jwtTokenProvider.validateToken(token)) {
                Long userPk = jwtTokenProvider.getUserPkFromToken(token);
                attributes.put("userPk", userPk);
                log.info("WebSocket 접속 userPk: {}", userPk);
            } else {
                log.warn("WebSocket 접속 토큰 없음 or 유효하지 않음");
            }
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }

}