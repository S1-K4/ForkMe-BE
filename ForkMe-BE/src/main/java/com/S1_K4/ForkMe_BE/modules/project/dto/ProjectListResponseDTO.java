package com.S1_K4.ForkMe_BE.modules.project.dto;

import com.S1_K4.ForkMe_BE.reference.position.dto.PositionResponseDTO;
import com.S1_K4.ForkMe_BE.reference.stack.dto.TechStackResponseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDate;
import java.util.List;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.project.dto
 * @fileName : ProjectResponseDTO
 * @date : 2025-08-05
 * @description : 프로젝트 목록 조회 DTO
 */
@Getter
@Builder
@AllArgsConstructor
@Jacksonized
@Schema(description = "프로젝트 목록 조회 DTO")
public class ProjectListResponseDTO {
    @Schema(description = "프로젝트 PK")
    private Long projectPk;

    @Schema(description = "프로젝트 프로필 PK")
    private Long projectProfilePk;

    @Schema(description = "프로젝트 팀장 userPK")
    private Long userPk;

    @Schema(description = "프로젝트 팀장 닉네임")
    private String nickname;

    @Schema(description = "프로젝트 프로필 명")
    private String projectProfileTitle;

    @Schema(description = "프로젝트 진행 상황")
    private String projectStatus;

    @Schema(description = "프로젝트 모집 분야")
    private List<PositionResponseDTO> positions;

    @Schema(description = "프로젝트 기술 스택")
    private List<TechStackResponseDTO> techStacks;

    @Schema(description = "프로젝트 모집 시작일")
    private LocalDate recruitmentStartDate;

    @Schema(description = "프로젝트 모집 마감일")
    private LocalDate recruitmentEndDate;

    @Schema(description = "예상 모집 인원")
    private int expectedMembers;
}