package com.S1_K4.ForkMe_BE.modules.comment.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
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
    private String comment;
}