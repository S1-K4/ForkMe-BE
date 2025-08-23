package com.S1_K4.ForkMe_BE.modules.alarm.dto;

import com.S1_K4.ForkMe_BE.global.common.common_enum.Yn;
import com.S1_K4.ForkMe_BE.modules.alarm.mongo_document.AlarmMessageDocument;
import lombok.*;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * @author : 김남이
 * @packageName : com.S1_K4.ForkMe_BE.modules.alarm.dto
 * @fileName : AlarmMessageDTO
 * @date : 2025-08-19
 * @description : 알람 전송 포맷 요청 입니다.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AlarmMessageRequest {

    private Long userPk; // 받을 유저
    private String alarmContent;
    private String alarmType; // ex."INVITE", "COMMENT", "SYSTEM"
    private Long referenceId;  // 관련된 리소스

    private Yn readYn;
    private Yn deletedYn; // 삭제 여부

    private LocalDateTime createdAt; //알림 생성 시간

    //MongoDB Document 변환
    public static AlarmMessageDocument toDocument(
            AlarmMessageRequest alarm
    ) {

        /** 방어 코드 **/
        // 1. createdAt 방어: 서비스에서 now 를 넣는 것이 원칙이지만, 혹시 null 이면 현재 시각으로 보정
        LocalDateTime created = alarm.getCreatedAt() != null
                ? alarm.getCreatedAt()
                : LocalDateTime.now(ZoneId.of("Asia/Seoul"));

        //2. AlarmType 대뮨자로만 저장
        // 쿼리 일관성을 위해 대문자로 저장
        String normalizedType = alarm.getAlarmType() != null ? alarm.getAlarmType().toUpperCase() : null;
        /*******/

        return AlarmMessageDocument.create(
                alarm.getUserPk(),
                alarm.getAlarmContent(),
                alarm.getAlarmType(),
                alarm.getReferenceId(),
                alarm.getCreatedAt()
        );

    }

    public static AlarmMessageRequest fromDocument(AlarmMessageDocument alarmDocument) {
        return AlarmMessageRequest.builder()
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
