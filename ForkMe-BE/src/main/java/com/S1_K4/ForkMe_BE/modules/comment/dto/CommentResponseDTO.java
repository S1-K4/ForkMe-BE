package com.S1_K4.ForkMe_BE.modules.comment.dto;

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
    String comment;
    Long commentPk;
    Long parentPk;
}