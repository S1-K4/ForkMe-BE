package com.S1_K4.ForkMe_BE.global.common.redis;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * @author : 김관중
 * @packageName : com.S1_K4.ForkMe_BE.global.common.redis
 * @fileName : WebSocketConfig
 * @date : 2025-08-05
 * @description : 웹소켓 설정 클래스 입니다
 */

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    //STOMP 메시지 브로커 구성
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        //클라이언트가 서버로부터 메시지 받는 경로
        registry.enableSimpleBroker("/sub");

        //클라이언트가 메시지를 보낼때 prefix
        registry.setApplicationDestinationPrefixes("/pub");
    }

    //클라이언트가 연결할 websocket 엔드포인트 설정
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") //CORS 허용
                .withSockJS();
    }
}
