package com.S1_K4.ForkMe_BE.reference.position.repository;

import com.S1_K4.ForkMe_BE.reference.position.entity.Position;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.reference.position.repository
 * @fileName : PositionRepsitory
 * @date : 2025-08-07
 * @description : 모집분야 Repository
 */
@Repository
public interface PositionRepsitory extends JpaRepository<Position, Long> {
}
