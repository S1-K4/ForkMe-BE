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

    // 좋아요 개수 count
    Long countByProjectProfile(ProjectProfile profile);

    // 특정 프로필의 좋아요 전체 삭제 (대량 삭제는 별도 버전 아래 참고)
    Long countByProjectProfile_ProjectProfilePk(Long projectProfilePk);

    // 특정 유저가 특정 프로필을 좋아요 했는지 여부
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
