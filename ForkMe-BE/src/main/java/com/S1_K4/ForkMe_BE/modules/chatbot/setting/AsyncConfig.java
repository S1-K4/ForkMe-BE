package com.S1_K4.ForkMe_BE.modules.chatbot.setting;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.chatbot.app
 * @fileName : AsyncConfig
 * @date : 2025-08-21
 * @description : 비동기 설정 추가
 * -> 긴 작업(GPT 호출, 로그 저장)을 스레드 풀로 빼서 응답 지연 최소화
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "botExecutor")
    public Executor botExecutor() {
        ThreadPoolTaskExecutor ex = new ThreadPoolTaskExecutor();
        ex.setCorePoolSize(2);
        ex.setMaxPoolSize(4);
        ex.setQueueCapacity(100);
        ex.setThreadNamePrefix("bot-async-");
        ex.initialize();
        return ex;
    }
}