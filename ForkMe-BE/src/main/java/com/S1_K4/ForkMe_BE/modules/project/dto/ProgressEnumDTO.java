package com.S1_K4.ForkMe_BE.modules.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.project.dto
 * @fileName : ProgressEnumDTO
 * @date : 2025-08-07
 * @description : 프로젝트 진행방식 enum DTO
 */

@Getter
@AllArgsConstructor
@Schema(description = "프로젝트 진행 방식", example = "ONLINE")
public class ProgressEnumDTO {

    @Schema(description = "진행방식", example = "ONLINE")
    private String name;        // 예: ONLINE
    @Schema(description = "진행방식 description", example = "온라인")
    private String description; // 예: 온라인
}