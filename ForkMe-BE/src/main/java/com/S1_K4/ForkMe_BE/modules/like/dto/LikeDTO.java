package com.S1_K4.ForkMe_BE.modules.like.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.like.dto
 * @fileName : LikeDTO
 * @date : 2025-08-11
 * @description : 좋아요 생성/삭제 시 호출하는 DTO
 */
@Getter
public class LikeDTO {
    @Schema(description = "좋아요 수 카운트", example = "2")
    private Long likeCount;
    @Schema(description = "좋아요 여부 체크", example = "true")
    private boolean isLiked;

    @Builder
    public LikeDTO(Long likeCount, boolean isLiked) {
        this.likeCount = likeCount;
        this.isLiked = isLiked;
    }
}