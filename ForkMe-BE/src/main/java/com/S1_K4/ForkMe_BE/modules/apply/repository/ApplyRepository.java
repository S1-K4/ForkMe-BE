package com.S1_K4.ForkMe_BE.modules.apply.repository;

import com.S1_K4.ForkMe_BE.modules.apply.dto.ApplyDto;
import com.S1_K4.ForkMe_BE.modules.apply.entity.Apply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.apply.repository
 * @fileName : ApplyRepository
 * @date : 2025-08-08
 * @description : 신청서 repository
 */
@Repository
public interface ApplyRepository extends JpaRepository<Apply,Long> {

    void deleteByProject_ProjectPk(Long projectPk);

    @Query("SELECT new com.S1_K4.ForkMe_BE.modules.apply.dto.ApplyDto(" +
            "a.applyPk, a.content, a.status, a.createdAt, a.updatedAt, a.user.userPk,a.project.projectPk,a.projectPosition.position.positionPk, a.projectPosition.position.positionName) " +
            "FROM Apply a " +
            "WHERE a.user.userPk = :userPk AND a.deletedYN = 'N' AND a.status IN(:stateList)")
    List<ApplyDto> findApplyByUserPkInState(Long userPk, List<String> stateList) ;

}
