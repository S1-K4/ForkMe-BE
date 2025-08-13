package com.S1_K4.ForkMe_BE.modules.apply.repository;

import com.S1_K4.ForkMe_BE.modules.apply.dto.ApplyListResponseDTO;
import com.S1_K4.ForkMe_BE.modules.apply.dto.ApplyDto;
import com.S1_K4.ForkMe_BE.modules.apply.entity.Apply;
import com.S1_K4.ForkMe_BE.modules.apply.enums.ApplyStatus;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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

    //유저&프로젝트 기준 신청서 조회
    @Query("""
    SELECT a FROM Apply a
    WHERE a.user.userPk = :userPk
      AND a.project.projectPk = :projectPk
      AND a.status IN (:statuses)
""")
    List<Apply> findBlockingApplies(
            @Param("userPk") Long userPk,
            @Param("projectPk") Long projectPk,
            @Param("statuses") List<ApplyStatus> statuses
    );

    //applyPk와 projectPk를 조건으로 프로젝트 신청서 조회
    Optional<Apply> findByApplyPkAndProject_ProjectPk(Long applyPk, Long projectPk);

    //projectPk를 조건으로 모든 신청서 조회
    @Query("""
        SELECT a
        FROM Apply a
        JOIN FETCH a.user u
        JOIN FETCH a.project p
        WHERE p.projectPk = :projectPk
        ORDER BY a.createdAt DESC
    """)
    List<Apply> findAllByProjectPk(@Param("projectPk") Long projectPk);


    @Query("SELECT new com.S1_K4.ForkMe_BE.modules.apply.dto.ApplyDto(" +
            "a.applyPk, a.content, a.status, a.createdAt, a.updatedAt, a.user.userPk,a.project.projectPk,a.projectPosition.position.positionPk, a.projectPosition.position.positionName) " +
            "FROM Apply a " +
            "WHERE a.user.userPk = :userPk AND a.deletedYN = 'N' AND a.status IN(:stateList)")
    List<ApplyDto> findApplyByUserPkInState(Long userPk, List<String> stateList) ;

}
