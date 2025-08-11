package com.S1_K4.ForkMe_BE.reference.stack.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author : 김종국
 * @packageName : com.S1_K4.ForkMe_BE.reference.stack.dto
 * @fileName : TechStackDto
 * @date : 2025-08-07
 * @description : 기술 스택 dto
 */
@ToString
@Getter
@AllArgsConstructor
public class TechStackDto {
    private Long techStackPk;
    private String techStackName;
}