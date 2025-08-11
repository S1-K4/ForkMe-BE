package com.S1_K4.ForkMe_BE.modules.like.repository;

import com.S1_K4.ForkMe_BE.modules.like.entity.Likes;
import com.S1_K4.ForkMe_BE.modules.project.entity.ProjectProfile;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.like.repository
 * @fileName : LikeRepository
 * @date : 2025-08-07
 * @description : 좋아요 repository
 */
@Repository
public interface LikeRepository extends JpaRepository<Likes, Long> {

    Long countByProjectProfile(ProjectProfile profile);

    Long countByProjectProfile_ProjectProfilePk(Long projectProfilePk);

    void deleteByProjectProfile_ProjectProfilePk(Long projectProfilePk);

    //특정 profile의 좋아요 여부를 체크하는 쿼리
    @Query("""
        SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END
        FROM Likes l
        WHERE l.user.userPk = :userPk
          AND l.projectProfile.projectProfilePk = :profilePk
    """)
    boolean existsByUserAndProjectProfile(@Param("userPk") Long userPk, @Param("profilePk") Long profilePk);
}
