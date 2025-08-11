package com.S1_K4.ForkMe_BE.modules.on_project.schedule.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author : 김송이
 * @packageName : com.S1_K4.ForkMe_BE.modules.on_project.schedule.dto
 * @fileName : ScheduleResponse
 * @date : 2025-08-10
 * @description : 일정관리 응답 dto 입니다.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScheduleResponse {
    private Long id;
    private String title;
    private LocalDateTime start;
    private LocalDateTime end;
    private Long userPk;  // 작성자 ID 추가
    private List<Long> scheduleMentionPk; // 멘션된 사용자 ID 목록
}