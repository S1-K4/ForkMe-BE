package com.S1_K4.ForkMe_BE.modules.user.entity;

import com.S1_K4.ForkMe_BE.reference.stack.entity.TechStack;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.user.entity
 * @fileName : UserTechStack
 * @date : 2025-08-04
 * @description : 유저 기술 스택 엔티티
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "user_tech_stack")
@Entity
public class UserTechStack {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_tech_stack_pk")
    private Long userTechStackPk;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_pk", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tech_pk", nullable = false)
    private TechStack techStack;

    public UserTechStack(User user, TechStack techStack) {
        this.user = user;
        this.techStack = techStack;
    }
}