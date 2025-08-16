package com.S1_K4.ForkMe_BE.modules.project.dto;

import com.S1_K4.ForkMe_BE.modules.project.enums.ProgressType;
import com.S1_K4.ForkMe_BE.modules.s3.dto.ProjectImageDTO;
import com.S1_K4.ForkMe_BE.reference.position.dto.PositionResponseDTO;
import com.S1_K4.ForkMe_BE.reference.stack.dto.TechStackResponseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.project.dto
 * @fileName : ProjectUpdateFormDTO
 * @date : 2025-08-08
 * @description : 프로젝트 수정 시 기존 데이터 보여주는 수정폼 DTO
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "프로젝트 수정폼 DTO")
public class ProjectUpdateFormDTO {
    @Schema(description = "프로젝트 PK")
    private Long projectPk;

    @Schema(description = "프로젝트 프로필 PK")
    private Long projectProfilePk;

    @Schema(description = "프로젝트 팀장 userPK")
    private Long userPk;

    // --Project 필드 --
    @Schema(description = "프로젝트 프로필 명")
    private String projectTitle;
    @Schema(description = "프로젝트 시작 일정")
    private LocalDate projectStartDate; 
    @Schema(description = "프로젝트 종료 일정")
    private LocalDate projectEndDate; 

    // --ProjectProfile 필드--
    @Schema(description = "프로젝트 프로필 명")
    private String projectProfileTitle;   
    @Schema(description = "프로젝트 프로필 본문")
    private String projectProfileContent;   
    @Schema(description = "프로젝트 진행 방식")
    private ProgressType progressType;    
    @Schema(description = "예상 모집 인원")
    private int expectedMembers;           
    @Schema(description = "프로젝트 모집 시작일")
    private LocalDate recruitmentStartDate;
    @Schema(description = "프로젝트 모집 마감일")
    private LocalDate recruitmentEndDate; 
    
    @Schema(description = "기술 스택 PK")
    private List<Long> techPks;         
    @Schema(description = "모집 분야 PK")
    private List<Long> positionPks;

    @Schema(description = "기술 스택 목록")
    private List<TechStackResponseDTO> techStacks;

    @Schema(description = "모집 분야 목록")
    private List<PositionResponseDTO> positions;

    @Schema(description = "이미지 리스트")
    private List<ProjectImageDTO> images;
}
