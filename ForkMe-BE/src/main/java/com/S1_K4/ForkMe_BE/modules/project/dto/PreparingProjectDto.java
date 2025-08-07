package com.S1_K4.ForkMe_BE.modules.project.dto;

import com.S1_K4.ForkMe_BE.modules.project.enums.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author : 김종국
 * @packageName : com.S1_K4.ForkMe_BE.modules.project.dto
 * @fileName : PreparingProjectDto
 * @date : 2025-08-07
 * @description : 사이드바 - 준비중인 프로젝트
 */

@Getter
@Setter
@AllArgsConstructor
public class PreparingProjectDto {
    private Long projectPk;
    private String projectName;
    private ProjectStatus projectStatus;
}