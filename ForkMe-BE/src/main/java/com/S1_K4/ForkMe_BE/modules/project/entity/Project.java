package com.S1_K4.ForkMe_BE.modules.project.entity;

import com.S1_K4.ForkMe_BE.global.common.entity.BaseTime;
import com.S1_K4.ForkMe_BE.modules.project.enums.ProjectStatus;
import com.S1_K4.ForkMe_BE.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

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
    
    
}