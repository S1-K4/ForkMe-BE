package com.S1_K4.ForkMe_BE.modules.project.entity;

import com.S1_K4.ForkMe_BE.reference.position.entity.Position;
import jakarta.persistence.*;
import lombok.*;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.project.entity
 * @fileName : ProjectPosition
 * @date : 2025-08-04
 * @description : 프로젝트 모집 포지션 엔티티
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
@Builder
@AllArgsConstructor
@Table(name="project_position")
public class ProjectPosition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="project_position_pk")
    private Long projectPositionPk;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="position_pk", nullable = false)
    private Position position;

    @ManyToOne(fetch = FetchType.LAZY )
    @JoinColumn(name="project_profile_pk", nullable = false)
    private ProjectProfile projectProfile;
}