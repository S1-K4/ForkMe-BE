package com.S1_K4.ForkMe_BE.modules.apply.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.apply.dto
 * @fileName : ApplyCreateRequestDTO
 * @date : 2025-08-11
 * @description : 신청서 작성 DTO
 */
@Getter
@Builder
public class ApplyCreateRequestDTO {
    private Long projectPk;
    private Long projectPositionPk;
    private List<Long> techStackPks;
    private String content;
}
