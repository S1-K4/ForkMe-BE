package com.S1_K4.ForkMe_BE.modules.apply.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : 김종국
 * @packageName : com.S1_K4.ForkMe_BE.modules.apply.dto
 * @fileName : ApplyTechStackDto
 * @date : 2025-08-11
 * @description : 지원서 기술 스택 dto
 */
@Getter
@AllArgsConstructor
public class ApplyTechStackDto {
    @Schema(description = "신청서 PK", example = "3")
    private Long applyPk;
    @Schema(description = "기술 스택 PK", example = "1")
    private Long techPk;
    @Schema(description = "기술 스택 명", example = "JAVA")
    private String techName;
}