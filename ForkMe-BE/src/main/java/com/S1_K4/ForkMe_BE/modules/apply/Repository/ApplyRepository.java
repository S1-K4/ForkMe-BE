package com.S1_K4.ForkMe_BE.modules.apply.Repository;

import com.S1_K4.ForkMe_BE.modules.apply.entity.Apply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.apply.Repository
 * @fileName : ApplyRepository
 * @date : 2025-08-08
 * @description : 신청서 Repository
 */
@Repository
public interface ApplyRepository extends JpaRepository<Apply,Long> {
    void deleteByProject_ProjectPk(Long projectPk);
}
