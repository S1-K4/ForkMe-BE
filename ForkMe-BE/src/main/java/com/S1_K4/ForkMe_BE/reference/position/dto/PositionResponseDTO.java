package com.S1_K4.ForkMe_BE.reference.position.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.reference.position.dto
 * @fileName : PositionResponseDTO
 * @date : 2025-08-05
 * @description : Position 응답 DTO
 */
@Getter
@AllArgsConstructor
public class PositionResponseDTO {
    private Long positionPk;
    private String positionName;
}
