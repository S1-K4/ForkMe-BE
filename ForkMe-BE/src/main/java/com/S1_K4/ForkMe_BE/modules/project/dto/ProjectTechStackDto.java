package com.S1_K4.ForkMe_BE.modules.project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : 김종국
 * @packageName : com.S1_K4.ForkMe_BE.modules.project.dto
 * @fileName : CompletedProjectTechStackDto
 * @date : 2025-08-08
 * @description : 완료된 프로젝트 팀원 리뷰 dto
 */
@Getter
@AllArgsConstructor
public class ProjectTechStackDto {
    private Long projectProfilePk;
    private Long techPk;
    private String techName;
}