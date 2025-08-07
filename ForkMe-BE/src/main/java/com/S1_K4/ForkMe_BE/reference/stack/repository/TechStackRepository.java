package com.S1_K4.ForkMe_BE.reference.stack.repository;

import com.S1_K4.ForkMe_BE.reference.stack.entity.TechStack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author : 김종국
 * @packageName : com.S1_K4.ForkMe_BE.reference.stack.repository
 * @fileName : TeckStackRepository
 * @date : 2025-08-06
 * @description : 기술 스택 리포지토리
 */
@Repository
public interface TechStackRepository extends JpaRepository<TechStack, Long> {
}
