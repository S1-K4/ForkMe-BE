package com.S1_K4.ForkMe_BE.modules.apply.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.apply.dto
 * @fileName : ApplyCreateRequestDTO
 * @date : 2025-08-11
 * @description : 신청서 작성 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplyCreateRequestDTO {
    @Schema(description = "프로젝트 Pk", example = "2")
    private Long projectPk;
    @Schema(description = "선택한 프로젝트 포지션 Pk", example = "1")
    private Long projectPositionPk;
    @Schema(description = "기술 스택 목록")
    private List<Long> techStackPks;
    @Schema(description = "프로젝트 Pk", example = "2")
    private String content;
}
