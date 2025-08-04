package com.S1_K4.ForkMe_BE.modules.on_project.review.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * @author : 김송이
 * @packageName : com.S1_K4.ForkMe_BE.modules.on_project.review.entity
 * @fileName : MemberReview
 * @date : 2025-08-04
 * @description : MemberReview entity 입니다
 */
@Entity
@Table(name = "member_review")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_review_pk")
    private Long id;

    @Column(name = "review", length = 100, nullable = false)
    private String review;

    // 후기 작성자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_pk", nullable = false)
    private User writer;

    // 후기 대상자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_pk2", nullable = false)
    private User target;

    // 후기 대상 프로젝트
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_pk", nullable = false)
    private Project project;
}