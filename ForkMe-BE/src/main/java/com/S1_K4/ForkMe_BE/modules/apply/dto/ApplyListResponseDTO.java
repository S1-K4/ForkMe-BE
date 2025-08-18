package com.S1_K4.ForkMe_BE.modules.apply.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "프로젝트 Pk", example = "2")
    private Long projectPk;
    @Schema(description = "유저 Pk", example = "1")
    private Long userPk;
    @Schema(description = "유저 닉네임", example = "test3")
    private String nickname;
    @Schema(description = "유저 프로필사진 url", example = "https://avatars.githubusercontent.com/u/97264463?v=4")
    private String profileUrl;
    @Schema(description = "신청서 PK", example = "1")
    private Long applyPk;
    @Schema(description = "신청서 상태", example = "수락")
    private String status;
    @Schema(description = "작성일자", example = "2025-07-22")
    private LocalDateTime createdAt;
}