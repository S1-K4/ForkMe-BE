package com.S1_K4.ForkMe_BE.modules.auth.repository;

import com.S1_K4.ForkMe_BE.modules.auth.entity.Auth;
import com.S1_K4.ForkMe_BE.modules.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author : 김종국
 * @packageName : com.S1_K4.ForkMe_BE.modules.auth.repository
 * @fileName : AuthRepository
 * @date : 2025-08-04
 * @description : Auth 레포지토리
 */

@Repository
public interface AuthRepository extends JpaRepository<Auth, Long> {

    Optional<Auth> findByUser(User user);

    Optional<Auth> findByRefreshToken(String refreshToken);
}
