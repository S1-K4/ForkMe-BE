package com.S1_K4.ForkMe_BE.modules.on_project.comment.dto;

import com.S1_K4.ForkMe_BE.modules.on_project.comment.entity.CommentInProject;
import lombok.*;

import java.time.LocalDateTime;

/**
 * @author : 김송이
 * @packageName : com.S1_K4.ForkMe_BE.modules.on_project.comment.dto
 * @fileName : CommentResponse
 * @date : 2025-08-10
 * @description : 워크스페이스 내 게시글 댓글 조회 dto
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponse {
    private Long commentInProjectPk;
    private String comment;
    private String name;
    private LocalDateTime createdAt;

    public static CommentResponse from(CommentInProject comment) {
        return CommentResponse.builder()
                .commentInProjectPk(comment.getCommentInProjectPk())
                .comment(comment.getComment())
                .name(comment.getUser().getName())  // 필요에 따라 nickname 등으로 변경 가능
                .createdAt(comment.getCreatedAt())
                .build();
    }
}