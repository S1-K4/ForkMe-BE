package com.S1_K4.ForkMe_BE.modules.on_project.schedule.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author : 김송이
 * @packageName : com.S1_K4.ForkMe_BE.modules.on_project.schedule.dto
 * @fileName : ScheduleCreateRequest
 * @date : 2025-08-10
 * @description : 일정 등록 생성 dto
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScheduleCreateRequest {
    private String title;
    private LocalDateTime start;
    private LocalDateTime end;

    private Long projectPk;   // 연관된 프로젝트 ID
    private Long userPk;      // 작성자(등록자) 유저 ID

    private List<Long> scheduleMentionPk;
}