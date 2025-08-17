package com.S1_K4.ForkMe_BE.modules.project.dto;

import com.S1_K4.ForkMe_BE.modules.project.entity.Project;
import com.S1_K4.ForkMe_BE.modules.project.entity.ProjectPosition;
import com.S1_K4.ForkMe_BE.modules.project.entity.ProjectProfile;
import com.S1_K4.ForkMe_BE.modules.project.entity.ProjectTechStack;
import com.S1_K4.ForkMe_BE.modules.s3.dto.ProjectImageDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.project.dto
 * @fileName : ProjectResponseDTO
 * @date : 2025-08-07
 * @description : ProjectResponseDTO
 */
@Getter
@Builder
@Schema(description = "프로젝트 Response DTO")
public class ProjectResponseDTO {
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
    @Schema(description = "프로젝트 진행 상황")
    private String projectStatus;

    // --ProjectProfile 필드--
    @Schema(description = "프로젝트 프로필 명")
    private String projectProfileTitle;
    @Schema(description = "프로젝트 프로필 본문")
    private String projectProfileContent;
    @Schema(description = "프로젝트 진행 방식")
    private String progressType;
    @Schema(description = "예상 모집 인원")
    private int expectedMembers;
    @Schema(description = "프로젝트 모집 시작일")
    private LocalDate recruitmentStartDate;
    @Schema(description = "프로젝트 모집 마감일")
    private LocalDate recruitmentEndDate;

    @Schema(description = "기술 스택 PK")
    private List<Long> techPks;
    @Schema(description = "포지션 PK")
    private List<Long> positionPks;

    @Schema(description = "이미지 리스트")
    private List<ProjectImageDTO> images;

    public static ProjectResponseDTO fromEntity(Project project, List<ProjectTechStack> techStacks, List<ProjectPosition> positions) {
        ProjectProfile profile = project.getProjectProfile();
        return ProjectResponseDTO.builder()
                .projectPk(project.getProjectPk())
                .projectProfilePk(profile.getProjectProfilePk())
                .userPk(project.getUser().getUserPk())
                .projectTitle(project.getProjectTitle())
                .projectStartDate(project.getProjectStartDate())
                .projectEndDate(project.getProjectEndDate())
                .projectStatus(project.getProjectStatus().getDescription())
                .projectProfileTitle(profile.getProjectProfileTitle())
                .projectProfileContent(profile.getProjectProfileContent())
                .progressType(profile.getProgressType().getDescription())
                .expectedMembers(profile.getExpectedMembers())
                .recruitmentStartDate(profile.getRecruitmentStartDate())
                .recruitmentEndDate(profile.getRecruitmentEndDate())
                .techPks(techStacks.stream()
                        .map(ts -> ts.getTechStack().getTechPk())
                        .toList())
                .positionPks(positions.stream()
                        .map(p->p.getPosition().getPositionPk())
                        .toList())
                .build();
    }
}