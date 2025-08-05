package com.S1_K4.ForkMe_BE.modules.project.dto;

import com.S1_K4.ForkMe_BE.reference.position.dto.PositionResponseDTO;
import com.S1_K4.ForkMe_BE.reference.stack.dto.TechStackResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

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
@AllArgsConstructor
public class ProjectListResponseDTO {
    //프로젝트 pk
    private Long projectPk;

    //프로젝트 프로필 pk
    private Long projectProfilePk;

    //프로젝트 팀장 pk
    private Long userPk;

    //프로젝트 프로필 제목
    private String projectProfileTitle;

    //프로젝트 진행상황
    private String projectStatus;

    //프로젝트 모집 분야
    private List<PositionResponseDTO> positions;

    //프로젝트 기술 스택
    private List<TechStackResponseDTO> techStacks;

    //모집 시작일
    private LocalDate recruitmentStartDate;

    //모집 마감일
    private LocalDate recruitmentEndDate;

    //예상 모집 인원
    private int expectedMembers;
}
