package com.S1_K4.ForkMe_BE.modules.alarm.mongo_document;

import com.S1_K4.ForkMe_BE.global.common.common_enum.Yn;
import com.S1_K4.ForkMe_BE.modules.chatting.chatting_enum.ChattingMessageType;
import com.S1_K4.ForkMe_BE.modules.chatting.mongo_document.ChattingMessageDocument;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

/**
 * @author : 김남이
 * @packageName : com.S1_K4.ForkMe_BE.modules.alarm.mongo_document
 * @fileName : AlarmMessageDocument
 * @date : 2025-08-20
 * @description : 알람 메세지를 몽고디비에 저장하기 위한 포맷입니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "alarm_messages")
public class AlarmMessageDocument {

    @Id
    private String alarmPk;

    private Long userPk; // 받을 유저
    private String alarmContent;
    private String alarmType; // ex."INVITE", "COMMENT", "SYSTEM"
    private Long referenceId;  // 관련된 리소스

    @Field("readYn")
    private Yn readYn = Yn.N;   // 읽음 여부

    @Field("deletedYn")
    private Yn deletedYn = Yn.N; // 삭제 여부

//    private LocalDateTime readAt; // 읽은 시각(읽지 않았을 경우 null)

    private LocalDateTime createdAt; //알림 생성 시간


    public static AlarmMessageDocument create(
           Long userPk, String alarmContent, String alarmType, Long referenceId, LocalDateTime now
    ) {
        return AlarmMessageDocument.builder()
                .userPk(userPk)
                .alarmContent(alarmContent)
                .alarmType(alarmType)
                .referenceId(referenceId)
                .readYn(Yn.N)
                .deletedYn(Yn.N)
                .createdAt(now)
                .build();
    }

    public void markAsRead() {
        this.readYn = Yn.Y;
    }

    public void markAsDeleted() {
        this.deletedYn = Yn.Y;
    }
}
