package com.S1_K4.ForkMe_BE.modules.project.dto;

import com.S1_K4.ForkMe_BE.modules.project.enums.ProgressType;
import com.S1_K4.ForkMe_BE.reference.position.dto.PositionResponseDTO;
import com.S1_K4.ForkMe_BE.reference.stack.dto.TechStackResponseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.project.dto
 * @fileName : ProjectCreateFormDTO
 * @date : 2025-08-07
 * @description : 프로젝트 생성 시, 사용자에게 보여줄 작성폼 DTO
 */

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "프로젝트 생성 폼 정보 DTO")
public class ProjectCreateFormDTO {
    private List<TechStackResponseDTO> techStacks;  //기술스택
    private List<PositionResponseDTO> positions;    //모집분야
    private List<Integer> expected_members;         //모집 예상인원(1~10명)
    private List<ProgressEnumDTO> progressType;     //진행방식


}