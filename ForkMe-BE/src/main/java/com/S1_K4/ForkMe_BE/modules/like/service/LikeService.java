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
    @Transactional(readOnly = true)
    boolean hasUserLikeProfile(Long userPk, Long profilePk);

    @Transactional(readOnly = true)
    Long countLike(Long profilePk);

    @Transactional
    LikeDTO createLike(Long userPk, Long profilePk);

    @Transactional
    LikeDTO deleteLike(Long userPk, Long profilePk);
}
