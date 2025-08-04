package com.S1_K4.ForkMe_BE.modules.apply.entity;

import com.S1_K4.ForkMe_BE.global.common.entity.BaseTime;
import com.S1_K4.ForkMe_BE.modules.apply.enums.ApplyStatus;
import com.S1_K4.ForkMe_BE.modules.project.entity.Project;
import com.S1_K4.ForkMe_BE.modules.project.entity.ProjectPosition;
import com.S1_K4.ForkMe_BE.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.apply.entity
 * @fileName : Apply
 * @date : 2025-08-04
 * @description : 신청서 엔티티
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
@Builder
@AllArgsConstructor
@Table(name="project_tech_stack")
public class Apply extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="apply_pk")
    private Long applyPk;

    @Column(name="content", nullable = false)
    private String content;

    @Column(name="status", nullable = false)
    private ApplyStatus status = ApplyStatus.PENDING;   //기본값 : 대기

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_pk", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="project_pk", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="project_position_pk", nullable = false)
    private ProjectPosition projectPosition;
}