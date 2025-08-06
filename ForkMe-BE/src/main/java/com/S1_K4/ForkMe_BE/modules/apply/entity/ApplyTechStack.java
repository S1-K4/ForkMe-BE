package com.S1_K4.ForkMe_BE.modules.apply.entity;

import com.S1_K4.ForkMe_BE.reference.stack.entity.TechStack;
import jakarta.persistence.*;
import lombok.*;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.apply.entity
 * @fileName : ApplyTechStack
 * @date : 2025-08-04
 * @description : 신청서 기술 스택 엔티티
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
@Builder
@AllArgsConstructor
@Table(name="apply_tech_stack")
public class ApplyTechStack {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="apply_tech_stack_pk")
    private Long applyTechStackPk;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="tech_pk", nullable = false)
    private TechStack techStack;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="apply_pk", nullable = false)
    private Apply apply;
}