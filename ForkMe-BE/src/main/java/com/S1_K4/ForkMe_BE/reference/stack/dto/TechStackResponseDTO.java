package com.S1_K4.ForkMe_BE.reference.stack.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.reference.stack.dto
 * @fileName : techStackResponseDTO
 * @date : 2025-08-05
 * @description : 기술스택 응답 DTO
 */
@Getter
@AllArgsConstructor
public class TechStackResponseDTO {
    private Long techPk;
    private String techName;
}
