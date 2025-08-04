package com.S1_K4.ForkMe_BE.modules.on_project.board.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * @author : 김송이
 * @packageName : com.S1_K4.ForkMe_BE.modules.on_project.board.entity
 * @fileName : BoardInProject
 * @date : 2025-08-04
 * @description : 프로젝트 안 게시판 Entity 입니다.
 */

@Entity
@Table(name = "board_in_project")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardInProject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_in_project_pk")
    private Long id;

    @Column(name = "title", length = 255, nullable = false)
    private String title;

    @Lob
    @Column(name = "content")
    private String content;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "deleted_yn", length = 1)
    private String deletedYn;

    // 🔁 관계 매핑 (ManyToOne) – Project
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_pk")
    private Project project;

    // 🔁 관계 매핑 (ManyToOne) – User
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_pk")
    private User user;

    // 🚨 created_at 자동 세팅하려면 Auditing 추가 필요
}