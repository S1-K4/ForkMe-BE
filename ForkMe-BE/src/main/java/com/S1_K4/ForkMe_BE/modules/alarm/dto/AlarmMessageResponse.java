package com.S1_K4.ForkMe_BE.modules.alarm.dto;

import com.S1_K4.ForkMe_BE.global.common.common_enum.Yn;
import com.S1_K4.ForkMe_BE.modules.alarm.mongo_document.AlarmMessageDocument;
import lombok.*;

import java.time.LocalDateTime;

/**
 * @author : 김남이
 * @packageName : com.S1_K4.ForkMe_BE.modules.alarm.dto
 * @fileName : AlarmMessageResponse
 * @date : 2025-08-21
 * @description : 알람 리스트 조회/삭제 응답 포맷입니다.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AlarmMessageResponse {

    private String alarmPk;
    private Long userPk; // 받을 유저
    private String alarmContent;
    private String alarmType; // ex."INVITE", "COMMENT", "SYSTEM"
    private Long referenceId;  // 관련된 리소스

    private Yn readYn = Yn.N;
    private Yn deletedYn = Yn.N; // 삭제 여부

    private LocalDateTime createdAt; //알림 생성 시간


    public static AlarmMessageResponse fromDocument(AlarmMessageDocument alarmDocument) {
        return AlarmMessageResponse.builder()
                .alarmPk(alarmDocument.getAlarmPk())
                .userPk(alarmDocument.getUserPk())
                .alarmContent(alarmDocument.getAlarmContent())
                .alarmType(alarmDocument.getAlarmType())
                .referenceId(alarmDocument.getReferenceId())
                .readYn(alarmDocument.getReadYn())
                .deletedYn(alarmDocument.getDeletedYn())
                .createdAt(alarmDocument.getCreatedAt())
                .build();
    }

}
