package com.S1_K4.ForkMe_BE.modules.chatting.dto;

import com.S1_K4.ForkMe_BE.modules.chatting.chatting_enum.ChattingMessageType;
import com.S1_K4.ForkMe_BE.modules.chatting.entity.ChattingMessage;
import com.S1_K4.ForkMe_BE.modules.chatting.entity.ChattingRoom;
import com.S1_K4.ForkMe_BE.modules.chatting.mongo_document.ChattingMessageDocument;
import com.S1_K4.ForkMe_BE.modules.user.entity.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * @author : 김남이
 * @packageName : com.S1_K4.ForkMe_BE.modules.chatting.dto
 * @fileName : ChattingMessageDto
 * @date : 2025-08-06
 * @description : redis 발행(Publish) 시 형식을 지정하는 dto 입니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChattingMessageDto {


    private Long chattingRoomPk;
    private Long userPk;
    private String nickName;   // ✅ 닉네임 추가
    private String message;

    private ChattingMessageType chattingMessageType; //"JOIN", "CHAT", LEAVE"

    //json 직렬화 - KST 타임으로 프론트에 보냄
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;  // 시간 타입으로 변경해야 @JsonFormat 이 의미 있음


    //MySQL 엔티티 변환
    public ChattingMessage toEntity(ChattingRoom chattingRoom, User user, LocalDateTime now){
        return ChattingMessage.create(chattingRoom, user, message,  chattingMessageType, now);
    }

    //MongoDB Document 변환
    public ChattingMessageDocument toDocument(String nickName, LocalDateTime now) {
        return ChattingMessageDocument.create(
                chattingRoomPk,
                userPk,
                nickName,
                message,
                chattingMessageType,
                now
        );

    }
}
