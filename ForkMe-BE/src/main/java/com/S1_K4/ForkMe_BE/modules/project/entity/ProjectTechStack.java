package com.S1_K4.ForkMe_BE.modules.project.entity;

import com.S1_K4.ForkMe_BE.reference.stack.entity.TechStack;
import jakarta.persistence.*;
import lombok.*;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.project.entity
 * @fileName : ProjectTechStack
 * @date : 2025-08-04
 * @description : 프로젝트 기술 스택 엔티티
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
@Builder
@AllArgsConstructor
@Table(name="project_tech_stack")
public class ProjectTechStack {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="project_teck_stack_pk")
    private Long projectTechStackPk;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="tech_pk", nullable = false)
    private TechStack techStack;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="project_profile_pk", nullable = false)
    private ProjectProfile projectProfile;

}