package com.S1_K4.ForkMe_BE.modules.apply.dto;

import com.S1_K4.ForkMe_BE.modules.apply.enums.ApplyStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.apply.dto
 * @fileName : ApplyListResponseDTO
 * @date : 2025-08-12
 * @description : 해당 프로젝트에 신청된 신청서 목록(팀장)
 */
@Getter
@Builder
public class ApplyListResponseDTO {
    private Long projectPk;
    private Long userPk;
    private String nickname;
    private String profileUrl;
    private String status;
    private LocalDateTime createdAt;
}