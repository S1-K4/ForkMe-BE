package com.S1_K4.ForkMe_BE.modules.project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : 김종국
 * @packageName : com.S1_K4.ForkMe_BE.modules.project.dto
 * @fileName : ProjectMemberCountDto
 * @date : 2025-08-08
 * @description : 프로젝트 참여자 수 dto
 */
@Getter
@AllArgsConstructor
public class ProjectMemberCountDto {
    private Long projectPk;
    private Long memberCount;
}