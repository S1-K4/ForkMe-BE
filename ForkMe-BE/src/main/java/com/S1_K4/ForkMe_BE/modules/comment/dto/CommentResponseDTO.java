package com.S1_K4.ForkMe_BE.modules.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.comment.dto
 * @fileName : CommentResponseDTO
 * @date : 2025-08-11
 * @description : 댓글 응답 DTO
 */

@Getter
@Builder
public class CommentResponseDTO {
    @Schema(description = "댓글 내용", example = "좋은 주제 같아요!")
    String comment;
    @Schema(description = "댓글 PK", example = "2")
    Long commentPk;
    @Schema(description = "상위 댓글 PK", example = "1")
    Long parentPk;
}