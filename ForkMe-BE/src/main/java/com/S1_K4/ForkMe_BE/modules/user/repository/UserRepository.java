package com.S1_K4.ForkMe_BE.modules.user.repository;

import aj.org.objectweb.asm.commons.Remapper;
import com.S1_K4.ForkMe_BE.modules.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
* @author         : 김종국
* @packageName    : com.S1_K4.ForkMe_BE.modules.user.repository
* @fileName       : UserRepository
* @date           : 2025-08-04
* @description    : user 레포지토리 인터페이스 
*/

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
