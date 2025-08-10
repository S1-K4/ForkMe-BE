package com.S1_K4.ForkMe_BE.modules.like.repository;

import com.S1_K4.ForkMe_BE.modules.like.entity.Likes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.like.repository
 * @fileName : LikeRepository
 * @date : 2025-08-07
 * @description : 좋아요 Repository
 */
@Repository
public interface LikeRepository extends JpaRepository<Likes, Long> {
    Long countByProjectProfile_ProjectProfilePk(Long projectProfilePk);

    void deleteByProjectProfile_ProjectProfilePk(Long projectProfilePk);
}
