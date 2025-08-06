package com.S1_K4.ForkMe_BE.modules.on_project.comment.entity;

import com.S1_K4.ForkMe_BE.global.common.entity.BaseTime;
import com.S1_K4.ForkMe_BE.modules.on_project.board.entity.BoardInProject;
import com.S1_K4.ForkMe_BE.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * @author : 김송이
 * @packageName : com.S1_K4.ForkMe_BE.modules.on_project.comment
 * @fileName : CommentInProject
 * @date : 2025-08-04
 * @description : 워크스페이스 안 게시판 댓글입니다.
 */


@Entity
@Table(name = "comment_in_project")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentInProject extends BaseTime {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_in_project_pk")
    private Long id;

    @Column(length = 255, nullable = false)
    private String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_in_project_pk", nullable = false)
    private BoardInProject board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_pk", nullable = false)
    private User user;

}