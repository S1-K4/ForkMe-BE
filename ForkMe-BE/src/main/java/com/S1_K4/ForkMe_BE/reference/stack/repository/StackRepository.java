package com.S1_K4.ForkMe_BE.reference.stack.repository;

import com.S1_K4.ForkMe_BE.reference.stack.entity.TechStack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.reference.stack.repository
 * @fileName : StackRepository
 * @date : 2025-08-07
 * @description : 기술 스택 repository
 */
@Repository
public interface StackRepository extends JpaRepository<TechStack, Long> {


}