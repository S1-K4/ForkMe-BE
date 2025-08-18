package com.S1_K4.ForkMe_BE.modules.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.comment.dto
 * @fileName : UpdateCommentDTO
 * @date : 2025-08-11
 * @description : 댓글 수정 요청 시 사용하는 DTO
 */

@Getter
@Builder
public class UpdateCommentDTO {
    @Schema(description = "댓글 내용", example = "좋은 주제네요!")
    private String comment;
}