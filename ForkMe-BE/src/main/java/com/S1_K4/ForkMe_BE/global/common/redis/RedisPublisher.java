package com.S1_K4.ForkMe_BE.global.common.redis;

import com.S1_K4.ForkMe_BE.modules.chatting.dto.ChattingMessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author : 김남이
 * @packageName : com.S1_K4.ForkMe_BE.global.common.redis
 * @fileName : RedisPublisher
 * @date : 2025-08-06
 * @description : redis 채팅 publish 클래스
 */
@Component
@RequiredArgsConstructor
public class RedisPublisher {

    private final RedisTemplate<String, Object> jsonRedisTemplate;

    public void publish(String topic, ChattingMessageDto message) {
        jsonRedisTemplate.convertAndSend(topic, message);
    }
}

