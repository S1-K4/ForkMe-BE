package com.S1_K4.ForkMe_BE.global.common.redis;

import com.S1_K4.ForkMe_BE.modules.chatting.dto.ChattingMessageDto;
import com.S1_K4.ForkMe_BE.modules.chatting.dto.ChattingUserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

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
    private final SimpMessagingTemplate messagingTemplate; // 추가

    public void publish(String topic, ChattingMessageDto message) {
        jsonRedisTemplate.convertAndSend(topic, message);
    }

    // 리스트 자체를 전달받는 방식으로 변경
    public void publishParticipantList(Long roomPk, List<ChattingUserDto> participants) {
        messagingTemplate.convertAndSend("/topic/chat/" + roomPk + "/members", participants);
    }
}


