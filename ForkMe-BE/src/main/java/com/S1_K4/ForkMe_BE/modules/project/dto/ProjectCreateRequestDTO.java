package com.S1_K4.ForkMe_BE.modules.project.dto;

import com.S1_K4.ForkMe_BE.modules.project.entity.*;
import com.S1_K4.ForkMe_BE.modules.project.enums.IsLeader;
import com.S1_K4.ForkMe_BE.modules.project.enums.ProgressType;
import com.S1_K4.ForkMe_BE.modules.project.enums.ProjectStatus;
import com.S1_K4.ForkMe_BE.modules.user.entity.User;
import com.S1_K4.ForkMe_BE.reference.position.entity.Position;
import com.S1_K4.ForkMe_BE.reference.stack.entity.TechStack;
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
 * @fileName : ProjectCreateRequestDTO
 * @date : 2025-08-07
 * @description : 프로젝트 생성 DTO
 */

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "프로젝트 생성 DTO")
public class ProjectCreateRequestDTO {
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
    private ProgressType progressType;      //진행방식
    private int expectedMembers;           //모집 예상 인원
    private LocalDate recruitmentStartDate; //모집 시작일
    private LocalDate recruitmentEndDate;   //모집 마감일

    private List<Long> techPk;              //기술 스택
    private List<Long> positionPk;          //모집 분야


    /*
    * toEntity
    * */

    public Project toProjectEntity(User user) {
        return Project.builder()
                .user(user)
                .projectTitle(this.projectProfileTitle)
                .projectStartDate(this.projectStartDate)
                .projectEndDate(this.projectEndDate)
                .projectStatus(ProjectStatus.PLANNING)
                .build();
    }

    public ProjectProfile toProjectProfileEntity(Project project) {
        return ProjectProfile.builder()
                .project(project)
                .projectProfileTitle(this.projectProfileTitle)
                .projectProfileContent(this.projectProfileContent)
                .progressType(this.progressType)
                .expectedMembers(this.expectedMembers)
                .recruitmentStartDate(this.recruitmentStartDate)
                .recruitmentEndDate(this.recruitmentEndDate)
                .build();
    }

    public ProjectMember toLeaderEntity(Project project, User user) {
        return ProjectMember.builder()
                .project(project)
                .user(user)
                .isLeader(IsLeader.LEADER)
                .build();
    }

    public List<ProjectTechStack> toProjectTechStackEntities(ProjectProfile profile, List<TechStack> techStackList) {
        return techStackList.stream()
                .map(tech -> ProjectTechStack.builder()
                        .projectProfile(profile)
                        .techStack(tech)
                        .build())
                .toList();
    }

    public List<ProjectPosition> toProjectPositionEntities(ProjectProfile project, List<Position> positionList) {
        return positionList.stream()
                .map(pos -> ProjectPosition.builder()
                        .projectProfile(project)
                        .position(pos)
                        .build())
                .toList();
    }
}