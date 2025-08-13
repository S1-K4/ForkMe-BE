package com.S1_K4.ForkMe_BE.modules.user.repository;

import com.S1_K4.ForkMe_BE.modules.user.dto.UserProfile;
import com.S1_K4.ForkMe_BE.modules.user.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author : 김종국
 * @packageName : com.S1_K4.ForkMe_BE.modules.user.repository
 * @fileName : UserRepository
 * @date : 2025-08-04
 * @description : user 레포지토리 인터페이스
 */

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByGitId(Long gitId);


    //유저의 기술스택 조회하는 쿼리
    @Query("""
    SELECT u FROM User u
    LEFT JOIN FETCH u.userTechStacks uts
    LEFT JOIN FETCH uts.techStack
    WHERE u.userPk = :userPk
""")
    Optional<User> findByIdWithTechStacks(@Param("userPk") Long userPk);

    @Query("SELECT new com.S1_K4.ForkMe_BE.modules.user.dto.UserProfile(" +
            "u.userPk, u.email, u.nickname, u.profileUrl)" +
            "FROM User u " +
            "WHERE u.userPk = :userPk")
    Optional<UserProfile> findByUserPk(Long userPk);
}
