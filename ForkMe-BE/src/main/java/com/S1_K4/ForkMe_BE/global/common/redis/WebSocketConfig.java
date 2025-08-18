package com.S1_K4.ForkMe_BE.global.common.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
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


    private final JwtHandshakeInterceptor jwtHandshakeInterceptor; // 접속중 로그인 추가

    //접속중 로그인 추가
    public WebSocketConfig(JwtHandshakeInterceptor jwtHandshakeInterceptor) {
        this.jwtHandshakeInterceptor = jwtHandshakeInterceptor;
    }


    //하트비트용 스케줄러 빈 (이름 충돌 피하려고 커스텀 이름 사용) : 빠른 접속/퇴장 확인용
    @Bean
    public TaskScheduler wsTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(1);
        scheduler.setThreadNamePrefix("ws-heartbeat-");
        scheduler.initialize();
        return scheduler;
    }

    //STOMP 메시지 브로커 구성
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        //클라이언트가 서버로부터 메시지 받는 경로 - 뷰어 수 & 채팅 모두 구독 가능
        registry.enableSimpleBroker("/sub", "/topic")
                .setTaskScheduler(wsTaskScheduler())
                .setHeartbeatValue(new long[]{10000, 10000});// [추가] 10s/10s

        //클라이언트가 메시지를 보낼때 prefix
        //발행 경로 병합 - 뷰어 수 & 채팅 발행
        registry.setApplicationDestinationPrefixes("/pub", "/app");
    }

    //클라이언트가 연결할 websocket 엔드포인트 설정
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
//                .addInterceptors(jwtHandshakeInterceptor) // 추가!
                .setAllowedOriginPatterns("*") //CORS 허용
                .withSockJS();

        registry.addEndpoint("ws-stomp") //채팅용 엔드포인트
                .addInterceptors(jwtHandshakeInterceptor) // 접속중 로그인 추가
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}
