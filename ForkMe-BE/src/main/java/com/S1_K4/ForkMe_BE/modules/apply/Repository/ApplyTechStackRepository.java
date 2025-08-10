package com.S1_K4.ForkMe_BE.modules.apply.Repository;

import com.S1_K4.ForkMe_BE.modules.apply.entity.ApplyTechStack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.apply
 * @fileName : ApplyTechStackRepository
 * @date : 2025-08-08
 * @description : 신청서 기술스택 repository
 */
@Repository
public interface ApplyTechStackRepository extends JpaRepository<ApplyTechStack, Long> {
    void deleteByApply_Project_ProjectPk(Long projectPk);
}
