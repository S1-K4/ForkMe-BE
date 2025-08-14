package com.S1_K4.ForkMe_BE.modules.project.dto;

import com.S1_K4.ForkMe_BE.modules.project.enums.ProgressType;
import com.S1_K4.ForkMe_BE.modules.project.enums.ProjectStatus;
import com.S1_K4.ForkMe_BE.reference.stack.dto.TechStackResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

/**
 * @author : 김종국
 * @packageName : com.S1_K4.ForkMe_BE.modules.project.dto
 * @fileName : CompletedProjectSummaryDto
 * @date : 2025-08-08
 * @description : 완료된 프로젝트 dto / 마이페이지에서 사용
 */
@Getter
@Setter
@AllArgsConstructor
public class CompletedProjectSummaryDto {

    // project
    private Long projectPk;
    private String projectTitle;
    private LocalDate projectStartDate;
    private LocalDate projectEndDate;
    private ProjectStatus projectStatus;

    // project_profile
    private Long projectProfilePk;
    private String projectProfileTitle;
    private ProgressType progressType;

    private Long memberCount;
    private List<TechStackResponseDTO> techStack;
    private List<String> review;

    public CompletedProjectSummaryDto(Long projectPk, String projectTitle, LocalDate projectStartDate, LocalDate projectEndDate, ProjectStatus projectStatus, Long projectProfilePk, String projectProfileTitle, ProgressType progressType) {
        this.projectPk = projectPk;
        this.projectTitle = projectTitle;
        this.projectStartDate = projectStartDate;
        this.projectEndDate = projectEndDate;
        this.projectStatus = projectStatus;
        this.projectProfilePk = projectProfilePk;
        this.projectProfileTitle = projectProfileTitle;
        this.progressType = progressType;
    }
}