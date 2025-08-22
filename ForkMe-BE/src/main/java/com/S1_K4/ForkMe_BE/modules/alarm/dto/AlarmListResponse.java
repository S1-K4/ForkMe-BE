package com.S1_K4.ForkMe_BE.modules.alarm.dto;

import lombok.*;

import java.util.List;

/**
 * @author : 김남이
 * @packageName : com.S1_K4.ForkMe_BE.modules.alarm.dto
 * @fileName : AlarmListResponse
 * @date : 2025-08-21
 * @description : 알람에서 카운트 수까지 반환하기위한 dto
 */
@Getter
@Setter
@Builder
public class AlarmListResponse {

    private final List<AlarmMessageResponse> alarms; // 알림 리스트
    private final int unreadCount;            // 안 읽은 알람 개수
}
