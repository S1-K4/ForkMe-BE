package com.S1_K4.ForkMe_BE.modules.on_project.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author : 김송이
 * @packageName : com.S1_K4.ForkMe_BE.modules.on_project.comment.dto
 * @fileName : CommentUpdateRequest
 * @date : 2025-08-12
 * @description : 워크스페이스 내 수정 dto
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentUpdateRequest {

    private Long userPk;
    private String comment;

}