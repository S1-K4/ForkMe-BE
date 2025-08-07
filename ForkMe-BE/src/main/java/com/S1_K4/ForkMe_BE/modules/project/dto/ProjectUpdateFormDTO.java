package com.S1_K4.ForkMe_BE.modules.project.dto;

import com.S1_K4.ForkMe_BE.modules.project.enums.ProgressType;
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
public class ProjectUpdateFormDTO {
    private Long projectPk;
    private Long projectProfilePk;
    private Long userPk;

    // --Project 필드 --
    private String projectTitle;
    private LocalDate projectStartDate; //프로젝트 시작 일정
    private LocalDate projectEndDate;   //프로젝트 종료 일정

    // --ProjectProfile 필드--
    private String projectProfileTitle;     //프로젝트 프로필 타이틀
    private String projectProfileContent;   //프로젝트 프로필 본문
    private ProgressType progressType;      //진행방식
    private int expectedMembers;            //모집 예상 인원
    private LocalDate recruitmentStartDate; //모집 시작일
    private LocalDate recruitmentEndDate;   //모집 마감일

    private List<Long> techPks;            // 선택한 기술스택 ID들
    private List<Long> positionPks;        // 선택한 모집분야 ID들
}
