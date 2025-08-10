package com.S1_K4.ForkMe_BE.reference.stack.repository;

import com.S1_K4.ForkMe_BE.reference.stack.entity.TechStack;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

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