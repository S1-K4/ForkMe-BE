package com.S1_K4.ForkMe_BE.modules.project.dto;

import com.S1_K4.ForkMe_BE.modules.project.entity.Project;
import com.S1_K4.ForkMe_BE.modules.project.entity.ProjectPosition;
import com.S1_K4.ForkMe_BE.modules.project.entity.ProjectProfile;
import com.S1_K4.ForkMe_BE.modules.project.entity.ProjectTechStack;
import com.S1_K4.ForkMe_BE.modules.project.enums.ProgressType;
import com.S1_K4.ForkMe_BE.modules.project.enums.ProjectStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.project.dto
 * @fileName : ProjectResponseDTO
 * @date : 2025-08-07
 * @description : ProjectResponseDTO
 */
@Getter
@Builder
public class ProjectResponseDTO {
    private Long projectPk;
    private Long projectProfilePk;
    private Long userPk;

    // --Project 필드 --
    private String projectTitle;
    private LocalDate projectStartDate;     //프로젝트 시작 일정
    private LocalDate projectEndDate;       //프로젝트 종료 일정
    private String projectStatus;           //프로젝트 상태

    // --ProjectProfile 필드--
    private String projectProfileTitle;     //프로젝트 프로필 타이틀
    private String projectProfileContent;   //프로젝트 프로필 본문
    private String progressType;            //진행방식
    private int expectedMembers;           //모집 예상 인원
    private LocalDate recruitmentStartDate; //모집 시작일
    private LocalDate recruitmentEndDate;   //모집 마감일

    private List<Long> techPks;
    private List<Long> positionPks;

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