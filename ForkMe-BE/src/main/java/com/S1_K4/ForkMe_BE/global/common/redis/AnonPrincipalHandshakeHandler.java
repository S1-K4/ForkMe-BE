package com.S1_K4.ForkMe_BE.global.common.redis;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.chatbot.app
 * @fileName : AnonPrincipalHandshakeHandler
 * @date : 2025-08-22
 * @description :
 */

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

/** 매 연결마다 랜덤 UUID Principal 부여 */
public class AnonPrincipalHandshakeHandler extends DefaultHandshakeHandler {

    @Override
    protected Principal determineUser(ServerHttpRequest request,
                                      WebSocketHandler wsHandler,
                                      Map<String, Object> attributes) {
        // 매 커넥션마다 새 UUID (탭별로 다름)
        String id = UUID.randomUUID().toString();
        return () -> id; // Principal#getName() -> UUID 문자열
    }
}