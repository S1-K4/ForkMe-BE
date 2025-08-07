package com.S1_K4.ForkMe_BE.modules.chatting.mongo_document;

import com.S1_K4.ForkMe_BE.modules.chatting.chatting_enum.ChattingMessageType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

/**
 * @author : 김남이
 * @packageName : com.S1_K4.ForkMe_BE.modules.chatting.mongo_document
 * @fileName : ChattingMessageDocument
 * @date : 2025-08-06
 * @description : mongoDB 저장 포맷을 지정하는 Document
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "chat_messages")
public class ChattingMessageDocument {

    @Id
    private String id;

    private Long chattingRoomPk;
    private Long userPk;
    private String nickName;   // ✅ 닉네임 저장
    private String message;

    @Field("chattingMessageType")
    private ChattingMessageType chattingMessageType;

    private LocalDateTime createdAt;

    public static ChattingMessageDocument create(
            Long chattingRoomPk, Long userPk, String nickName, String message, ChattingMessageType chattingMessageType, LocalDateTime now
    ) {
        return ChattingMessageDocument.builder()
                .chattingRoomPk(chattingRoomPk)
                .userPk(userPk)
                .nickName(nickName)
                .message(message)
                .chattingMessageType(chattingMessageType)
                .createdAt(now)
                .build();
    }
}
