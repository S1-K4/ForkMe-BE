package com.S1_K4.ForkMe_BE.modules.like.dto;

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
    private Long likeCount;
    private boolean isLiked;

    @Builder
    public LikeDTO(Long likeCount, boolean isLiked) {
        this.likeCount = likeCount;
        this.isLiked = isLiked;
    }
}