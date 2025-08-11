package com.S1_K4.ForkMe_BE.modules.comment.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.comment.dto
 * @fileName : CreateCommentDTO
 * @date : 2025-08-11
 * @description : 댓글 추가/수정 requestDTO
 */

@Getter
public class CreateCommentDTO {
    private String comment;
    private Long parentPk;

    @Builder
    public CreateCommentDTO(String comment, Long parentPk){
        this.comment = comment;
        this.parentPk =parentPk;
    }
}