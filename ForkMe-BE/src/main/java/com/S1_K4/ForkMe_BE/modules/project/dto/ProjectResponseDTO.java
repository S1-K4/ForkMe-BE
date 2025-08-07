package com.S1_K4.ForkMe_BE.modules.project.dto;

import com.S1_K4.ForkMe_BE.modules.project.enums.ProgressType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

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
    private String projectTitle;
    private LocalDate projectStartDate;
    private LocalDate projectEndDate;

    private String projectProfileTitle;
    private String projectProfileContent;
    private ProgressType progressType;
    private int expectedMembers;
    private LocalDate recruitmentStartDate;
    private LocalDate recruitmentEndDate;
}