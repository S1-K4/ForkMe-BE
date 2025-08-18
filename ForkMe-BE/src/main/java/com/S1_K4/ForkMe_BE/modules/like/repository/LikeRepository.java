package com.S1_K4.ForkMe_BE.modules.like.repository;

import com.S1_K4.ForkMe_BE.modules.like.entity.Likes;
import com.S1_K4.ForkMe_BE.modules.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.like.repository
 * @fileName : LikeRepository
 * @date : 2025-08-07
 * @description : 좋아요 repository
 */
@Repository
public interface LikeRepository extends JpaRepository<Likes, Long> {

    // 해당 ProjectProfile의 좋아요 개수 count
    Long countByProjectProfile_ProjectProfilePk(Long projectProfilePk);

    //해당 ProjectProfile과 연관된 좋아요 필드 삭제
    void deleteByProjectProfile_ProjectProfilePk(Long projectProfilePk);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM Likes i WHERE i.projectProfile.projectProfilePk IN (:projectProfilePkList)")
    void deleteByProjectProfile_ProjectProfilePkInBulk(List<Long> projectProfilePkList);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM Likes i WHERE i.user = (:user)")
    void deleteByUser(User user);

    //특정 profile의 좋아요 여부를 체크하는 쿼리
    @Query("""
        SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END
        FROM Likes l
        WHERE l.user.userPk = :userPk
          AND l.projectProfile.projectProfilePk = :profilePk
    """)
    boolean existsByUserAndProjectProfile(@Param("userPk") Long userPk, @Param("profilePk") Long profilePk);

    //user가 해당 projectProfile에 눌렀던 좋아요 엔티티 1건을 조회하는 메서드 -> 이 사용자가 이 프로젝트에 좋아요를 눌렀는지 체크하는 확인 용도
    Optional<Likes> findByUser_UserPkAndProjectProfile_ProjectProfilePk(Long userPk, Long projectProfilePk);
}


