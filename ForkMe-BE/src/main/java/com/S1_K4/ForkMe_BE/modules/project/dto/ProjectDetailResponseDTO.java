package com.S1_K4.ForkMe_BE.modules.project.dto;

import com.S1_K4.ForkMe_BE.reference.position.dto.PositionResponseDTO;
import com.S1_K4.ForkMe_BE.reference.stack.dto.TechStackResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.project.dto
 * @fileName : ProjectDetailResponseDTO
 * @date : 2025-08-05
 * @description : 프로젝트 상세 조회 DTO
 */
@Getter
@Builder
public class ProjectDetailResponseDTO {
    //프로젝트 pk
    private Long projectPk;

    //프로젝트 프로필 pk
    private Long projectProfilePk;

    //프로젝트 팀장 pk
    private Long userPk;

    //프로젝트 프로필 제목
    private String projectProfileTitle;

    //프로젝트 프로필 본문
    private String projectProfileContent;

    //프로젝트 진행상황
    private String projectStatus;

    //프로젝트 진행방식
    private String progressType;

    //프로젝트 모집 분야
    private List<PositionResponseDTO> positions;

    //프로젝트 기술 스택
    private List<TechStackResponseDTO> techStacks;

    //모집 시작일
    private LocalDate recruitmentStartDate;

    //모집 마감일
    private LocalDate recruitmentEndDate;

    //프로젝트 시작 일정
    private LocalDate projectStartDate;

    private LocalDate projectEndDate;

    //예상 모집 인원
    private int expectedMembers;
}
