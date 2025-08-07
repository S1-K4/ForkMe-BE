package com.S1_K4.ForkMe_BE.modules.project.entity;

import com.S1_K4.ForkMe_BE.modules.project.enums.IsLeader;
import com.S1_K4.ForkMe_BE.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.project.entity
 * @fileName : ProjectMember
 * @date : 2025-08-04
 * @description : 프로젝트 참여 인원 엔티티
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
@Builder
@AllArgsConstructor
@Table(name="project_member")
public class ProjectMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="project_member_pk")
    private Long projectMemberPk;

    @Enumerated(EnumType.STRING)
    @Column(name="is_leader")
    private IsLeader isLeader;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_pk", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="project_pk", nullable = false)
    private Project project;
}