package com.S1_K4.ForkMe_BE.modules.on_project.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
* @author         : 김송이
* @packageName    : com.S1_K4.ForkMe_BE.modules.on_project.comment.dto
* @fileName       : CommentCreateRequest
* @date           : 2025-08-10
* @description    : 워크스페이스 내 게시글 댓글 생성 dto
*/

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentCreateRequest {


    private Long boardInProjectPk;
    private Long userPk;
    private String comment;
}