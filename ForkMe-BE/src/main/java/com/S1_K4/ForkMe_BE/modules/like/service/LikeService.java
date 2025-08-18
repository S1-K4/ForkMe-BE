package com.S1_K4.ForkMe_BE.modules.like.service;

import com.S1_K4.ForkMe_BE.modules.like.dto.LikeDTO;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.like.service
 * @fileName : LikeService
 * @date : 2025-08-11
 * @description :LikeService
 */
public interface LikeService {
    //좋아요 여부 조회
    boolean hasUserLikeProfile(Long userPk, Long profilePk);
    
    //좋아요 수 COUNT
    Long countLike(Long profilePk);
    
    //좋아요 추가
    LikeDTO createLike(Long userPk, Long profilePk);
    
    //좋아요 삭제
    LikeDTO deleteLike(Long userPk, Long profilePk);
}
