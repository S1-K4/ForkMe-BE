package com.S1_K4.ForkMe_BE.global.common.redis;

import com.S1_K4.ForkMe_BE.modules.alarm.dto.AlarmMessageRequest;
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
    private final RedisPublisher redisPublisher; // 추가

    // LocalDateTime 지원을 위한 설정 추가
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

//    @Override
//    public void onMessage(Message message, byte[] pattern) {
//        try {
//            ChattingMessageDto chattingMessage = objectMapper.readValue(message.getBody(), ChattingMessageDto.class);
//            ChattingMessageType chattingMessageType = chattingMessage.getChattingMessageType();
//
//            Long roomPk = chattingMessage.getChattingRoomPk(); // 반복 제거용
//
//            //타입 분기
//            switch (chattingMessageType) {
//                case CHAT -> messagingTemplate.convertAndSend("/topic/chat/" + chattingMessage.getChattingRoomPk(), chattingMessage);
//                case JOIN, LEAVE -> {
//                    messagingTemplate.convertAndSend("/topic/chat/" + roomPk, chattingMessage); // 기존 유지
//
//                }
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String channel = new String(message.getChannel());

            System.out.println("Redis 구독 메시지 수신: channel=" + channel);
            System.out.println("raw body=" + new String(message.getBody()));

            // 1. 알림 채널이면 알림 메시지로 역직렬화 및 전송
            if (channel.startsWith("alarm:")) {
                AlarmMessageRequest alarm = objectMapper.readValue(message.getBody(), AlarmMessageRequest.class);
                Long userPk = alarm.getUserPk();
                messagingTemplate.convertAndSend("/topic/alarm/" + userPk, alarm );
                return; // 아래 채팅 로직 실행하지 않음
            }

            // 2. 채팅 메시지는 기존처럼 처리
            ChattingMessageDto chattingMessage = objectMapper.readValue(message.getBody(), ChattingMessageDto.class);
            ChattingMessageType chattingMessageType = chattingMessage.getChattingMessageType();
            Long roomPk = chattingMessage.getChattingRoomPk();

            switch (chattingMessageType) {
                case CHAT -> messagingTemplate.convertAndSend("/topic/chat/" + roomPk, chattingMessage);
                case JOIN, LEAVE -> messagingTemplate.convertAndSend("/topic/chat/" + roomPk, chattingMessage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
