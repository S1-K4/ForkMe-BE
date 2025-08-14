package com.S1_K4.ForkMe_BE.reference.position.repository;

import com.S1_K4.ForkMe_BE.reference.position.entity.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.reference.position.repository
 * @fileName : PositionRepository
 * @date : 2025-08-07
 * @description : 모집분야 repository
 */
@Repository
public interface PositionRepository extends JpaRepository<Position, Long> {
}
