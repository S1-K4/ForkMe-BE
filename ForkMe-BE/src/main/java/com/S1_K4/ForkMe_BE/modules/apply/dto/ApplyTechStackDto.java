package com.S1_K4.ForkMe_BE.modules.apply.dto;

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
    private Long applyPk;
    private Long techPk;
    private String techName;
}