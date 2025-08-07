package com.S1_K4.ForkMe_BE.modules.project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.project.dto
 * @fileName : ProgressEnumDTO
 * @date : 2025-08-07
 * @description : 프로젝트 진행방식 enum DTO
 */

@Getter
@AllArgsConstructor
public class ProgressEnumDTO {
    private String name;        // 예: ONLINE
    private String description; // 예: 온라인
}