package com.S1_K4.ForkMe_BE.modules.apply.dto;

import com.S1_K4.ForkMe_BE.reference.position.dto.PositionResponseDTO;
import com.S1_K4.ForkMe_BE.reference.stack.dto.TechStackResponseDTO;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.apply.dto
 * @fileName : ApplyCreateFormDTO
 * @date : 2025-08-11
 * @description : 신청서 작성 폼 DTO
 */
@Getter
@Builder
public class ApplyCreateFormDTO {
    private Long projectPk;
    private List<TechStackResponseDTO> techStacks;  //기술스택
    private List<PositionResponseDTO> positions;    //모집분야
}
