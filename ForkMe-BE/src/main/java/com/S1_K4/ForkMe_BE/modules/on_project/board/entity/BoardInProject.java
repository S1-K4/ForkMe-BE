package com.S1_K4.ForkMe_BE.modules.on_project.board.entity;

import com.S1_K4.ForkMe_BE.global.common.entity.BaseTime;
import com.S1_K4.ForkMe_BE.modules.on_project.comment.entity.CommentInProject;
import com.S1_K4.ForkMe_BE.modules.project.entity.Project;
import com.S1_K4.ForkMe_BE.modules.s3.entity.S3File;
import com.S1_K4.ForkMe_BE.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

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
public class BoardInProject extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_in_project_pk")
    private Long boardInProjectPk;

    @Column(name = "title", length = 255, nullable = false)
    private String title;

    @Lob
    @Column(name = "content")
    private String content;


    // 🔁 관계 매핑 (ManyToOne) – Project
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_pk")
    private Project project;

    // 🔁 관계 매핑 (ManyToOne) – User
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_pk")
    private User user;

    @OneToMany(mappedBy = "boardInProject", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<S3File> files;

    @OneToMany(mappedBy ="boardInProject", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<CommentInProject> comments;

    //정적 팩토리 메소드 추가
    public static BoardInProject create(String title, String content, Project project, User user){
        return BoardInProject.builder()
                .title(title)
                .content(content)
                .project(project)
                .user(user)
                .build();
    }
}