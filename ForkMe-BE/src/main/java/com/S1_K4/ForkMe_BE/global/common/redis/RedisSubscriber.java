package com.S1_K4.ForkMe_BE.global.common.redis;

import com.S1_K4.ForkMe_BE.modules.chatting.chatting_enum.ChattingMessageType;
import com.S1_K4.ForkMe_BE.modules.chatting.dto.ChattingMessageDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * @author : 김남이
 * @packageName : com.S1_K4.ForkMe_BE.global.common.redis
 * @fileName : RedisSubscriber
 * @date : 2025-08-06
 * @description : chatting 서비스 redis 구독을 관리하는 클래스
 */
@Component
@RequiredArgsConstructor
public class RedisSubscriber implements MessageListener {

    private final SimpMessagingTemplate messagingTemplate;

    // LocalDateTime 지원을 위한 설정 추가
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            ChattingMessageDto chattingMessage = objectMapper.readValue(message.getBody(), ChattingMessageDto.class);
            ChattingMessageType chattingMessageType = chattingMessage.getChattingMessageType();

            //타입 분기
            switch (chattingMessageType) {
                case CHAT -> messagingTemplate.convertAndSend("/topic/chat/" + chattingMessage.getChattingRoomPk(), chattingMessage);
                case JOIN, LEAVE -> messagingTemplate.convertAndSend("/topic/chat/" +  chattingMessage.getChattingRoomPk() + "/members", chattingMessage);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
