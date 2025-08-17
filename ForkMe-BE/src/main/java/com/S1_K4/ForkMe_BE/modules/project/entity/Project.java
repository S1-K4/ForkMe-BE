package com.S1_K4.ForkMe_BE.modules.project.entity;

import com.S1_K4.ForkMe_BE.global.common.entity.BaseTime;
import com.S1_K4.ForkMe_BE.global.exception.CustomException;
import com.S1_K4.ForkMe_BE.modules.project.dto.ProjectUpdateFormDTO;
import com.S1_K4.ForkMe_BE.modules.project.enums.ProjectStatus;
import com.S1_K4.ForkMe_BE.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Objects;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.project.entity
 * @fileName : Project
 * @date : 2025-08-04
 * @description : project 엔티티
 */
@Entity
@Table(name = "project")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Project extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="project_pk")
    private Long projectPk;

    //프로젝트 프로필과 양방향
    @OneToOne(mappedBy = "project", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private ProjectProfile projectProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_pk", nullable = false)
    private User user;

    @Column(name="project_title", nullable = false)
    private String projectTitle;

    @Enumerated(EnumType.STRING)
    @Column(name="project_status", nullable = false)
    private ProjectStatus projectStatus = ProjectStatus.PLANNING;  //기본값 : 기획
    
    @Column(name="project_start_date", nullable = false)
    private LocalDate projectStartDate;
    
    @Column(name="project_end_date", nullable = false)
    private LocalDate projectEndDate;

    //프로젝트 수정시 사용하는 메서드
    public void updateIfChanged(ProjectUpdateFormDTO dto) {
        if (!Objects.equals(this.projectTitle, dto.getProjectTitle())) {
            this.projectTitle = dto.getProjectProfileTitle();
        }
        if (!Objects.equals(this.projectStartDate, dto.getProjectStartDate())) {
            this.projectStartDate = dto.getProjectStartDate();
        }
        if (!Objects.equals(this.projectEndDate, dto.getProjectEndDate())) {
            this.projectEndDate = dto.getProjectEndDate();
        }
    }

    /*
    * 프로젝트 상태 수정 시 사용하는 메서드
    * */
    //기획 -> 모집
    public void recruiting(){
        if (this.projectStatus != ProjectStatus.PLANNING) {
            throw new CustomException(CustomException.ErrorCode.PROJECT_STATUS_CHANGE);
        }
        this.projectStatus = ProjectStatus.RECRUITING;
    }

    //모집 or 충원 -> 진행중
    public void progress(){
        if (this.projectStatus != ProjectStatus.RECRUITING && this.projectStatus != ProjectStatus.ADDING) {
            throw new CustomException(CustomException.ErrorCode.PROJECT_STATUS_CHANGE);
        }
        this.projectStatus = ProjectStatus.IN_PROGRESS;
    }

    //진행중 -> 충원
    public void adding(){
        if (this.projectStatus != ProjectStatus.IN_PROGRESS) {
            throw new CustomException(CustomException.ErrorCode.PROJECT_STATUS_CHANGE);
        }
        this.projectStatus = ProjectStatus.ADDING;
    }

    //진행중 -> 종료
    public void complete(){
        if (this.projectStatus != ProjectStatus.IN_PROGRESS) {
            throw new CustomException(CustomException.ErrorCode.PROJECT_STATUS_CHANGE);
        }
        this.projectStatus = ProjectStatus.COMPLETED;
    }

    //프로젝트 명 변경 메서드
    public void updateProjectTitle(String projectTitle){
        this.projectTitle = projectTitle;
    }
}