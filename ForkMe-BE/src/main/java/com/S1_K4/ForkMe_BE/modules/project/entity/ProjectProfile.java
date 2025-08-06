package com.S1_K4.ForkMe_BE.modules.project.entity;

import com.S1_K4.ForkMe_BE.global.common.entity.BaseTime;
import com.S1_K4.ForkMe_BE.modules.project.enums.ProgressType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.project.entity
 * @fileName : ProjectProfile
 * @date : 2025-08-04
 * @description : ProjectProfile 엔티티
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
@Builder
@AllArgsConstructor
@Table(name="project_profile")
public class ProjectProfile extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="project_profile_pk", nullable = false)
    private Long projectProfilePk;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_pk", nullable = false)
    private Project project;

    @Column(name ="project_profile_title", nullable = false)
    private String projectProfileTitle;

    @Column(name="project_profile_content", columnDefinition = "TEXT", nullable = false)
    private String projectProfileContent;

    @Column(name="progress_type", nullable = false)
    private ProgressType progressType;

    @Column(name="expected_member", nullable = false)
    private int expectedMember;

    @Column(name="recruitment_start_date", nullable = false)
    private LocalDate recruitmentStartDate; //모집 시작 날짜

    @Column(name="recruitment_end_date", nullable = false)
    private LocalDate recruitmentEndDate;   //모집 종료 기간
}