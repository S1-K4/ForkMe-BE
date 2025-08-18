package com.S1_K4.ForkMe_BE.modules.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : 김종국
 * @packageName : com.S1_K4.ForkMe_BE.modules.project.dto
 * @fileName : ProjectTechStackDto
 * @date : 2025-08-08
 * @description : 프로젝트 기술스택 DTO
 */
@Getter
@AllArgsConstructor
@Schema(description = "프로젝트 기술스택 DTO")
public class ProjectTechStackDto {
    @Schema(description = "프로젝트 프로필 PK")
    private Long projectProfilePk;
    @Schema(description = "기술 스택 PK")
    private Long techPk;
    @Schema(description = "기술 스택 명")
    private String techName;
}