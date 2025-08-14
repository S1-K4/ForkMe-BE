package com.S1_K4.ForkMe_BE.modules.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "댓글 내용", example = "좋은 주제네요")
    private String comment;
    @Schema(description = "상위 댓글 PK", example = "2")
    private Long parentPk;

    @Builder
    public CreateCommentDTO(String comment, Long parentPk){
        this.comment = comment;
        this.parentPk =parentPk;
    }
}