package com.S1_K4.ForkMe_BE.modules.project.entity;

import com.S1_K4.ForkMe_BE.global.common.entity.BaseTime;
import com.S1_K4.ForkMe_BE.modules.on_project.comment.entity.Comment;
import com.S1_K4.ForkMe_BE.modules.project.enums.ProgressType;
import com.S1_K4.ForkMe_BE.modules.s3.Entity.S3Image;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    @Enumerated(EnumType.STRING)
    @Column(name="progress_type", nullable = false)
    private ProgressType progressType;

    @Column(name="expected_members", nullable = false)
    private int expectedMembers;

    @Column(name="recruitment_start_date", nullable = false)
    private LocalDate recruitmentStartDate; //모집 시작 날짜

    @Column(name="recruitment_end_date", nullable = false)
    private LocalDate recruitmentEndDate;   //모집 종료 기간

    @Builder.Default
    @OneToMany(mappedBy = "projectProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<S3Image> images = new ArrayList<>();

    @OneToMany(mappedBy = "projectProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();
}