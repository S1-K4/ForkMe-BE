package com.S1_K4.ForkMe_BE.reference.position.repository;

import com.S1_K4.ForkMe_BE.reference.stack.entity.TechStack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.reference.position.repository
 * @fileName : TechStackRepository
 * @date : 2025-08-11
 * @description : TechStackRepository
 */
@Repository
public interface TechStackRepository extends JpaRepository<TechStack, Long> {
}
