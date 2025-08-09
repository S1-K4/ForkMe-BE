package com.S1_K4.ForkMe_BE.modules.chatting.entity;

import com.S1_K4.ForkMe_BE.global.common.entity.BaseTime;
import com.S1_K4.ForkMe_BE.modules.chatting.chatting_enum.ChattingMessageType;
import com.S1_K4.ForkMe_BE.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author : 김남이
 * @packageName : com.S1_K4.ForkMe_BE.modules.chatting.entity
 * @fileName : ChattingMessage
 * @date : 2025-08-06
 * @description : MySQL에 채팅 내역 저장하기 위한 엔티티입니다.
 */
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "chatting_message")
public class ChattingMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chatting_message_pk")
    private Long chattingMessagePk;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatting_room_pk", nullable = false)
    private ChattingRoom chattingRoomPk;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_pk", nullable = false)
    private User user;

    @Column(name = "message", length = 255)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "chatting_message_type", nullable = false, length = 10)
    private ChattingMessageType chattingMessageType;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public static ChattingMessage create(
            ChattingRoom chattingRoomPk, User userPk, String message, ChattingMessageType chattingMessageType, LocalDateTime now){
        return ChattingMessage.builder()
                .chattingRoomPk(chattingRoomPk)
                .user(userPk)
                .message(message)
                .chattingMessageType(chattingMessageType)
                .createdAt(now)
                .build();
    }

}
