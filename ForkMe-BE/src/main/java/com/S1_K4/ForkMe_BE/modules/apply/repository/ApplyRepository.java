package com.S1_K4.ForkMe_BE.modules.apply.repository;

import com.S1_K4.ForkMe_BE.modules.apply.dto.MyApplyListResponseDto;
import com.S1_K4.ForkMe_BE.modules.apply.entity.Apply;
import com.S1_K4.ForkMe_BE.modules.apply.enums.ApplyStatus;
import com.S1_K4.ForkMe_BE.modules.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    //해당 projectPk를 가지는 신청서 삭제
    void deleteByProject_ProjectPk(Long projectPk);

    //해당 유저가 해당 프로젝트에 대해 상태가 PENDING 또는 APPROVED인 신청서가 있다면 조회
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

    //특정 프로젝트pk를 가진 신청서중 PENDING(대기)상태인 신청서 모두 조회
    @Query("""
        select a 
        from Apply a
        where a.project.projectPk = :projectPk
          and a.status = com.S1_K4.ForkMe_BE.modules.apply.enums.ApplyStatus.PENDING
    """)
    List<Apply> findPendingAppliesByProjectPk(@Param("projectPk") Long projectPk);


    //특정 프로젝트(projectPk)안에서 특정 신청서(applyPk)조회
    Optional<Apply> findByApplyPkAndProject_ProjectPk(Long applyPk, Long projectPk);

    //프로젝트의 신청서 단건 상세조회(JOIN FETCH: User/Project/ProjectProfile/Position/ApplyTechStacks/TechStack  한번에 조회)
    @Query("""
        select distinct a
        from Apply a
        join fetch a.user u
        join fetch a.project p
        left join fetch a.projectPosition pp
        left join fetch pp.position pos
        left join fetch a.applyTechStacks ats
        left join fetch ats.techStack ts
        left join fetch a.project.projectProfile pf
        where a.applyPk = :applyPk
          and p.projectPk = :projectPk
    """)
        Optional<Apply> findDetailById(@Param("applyPk") Long applyPk,
                                       @Param("projectPk") Long projectPk);

    //projectPk를 조건으로 모든 신청서 조회 단, status가 'CANCEL'인 (취소된 신청서) 신청서는 제외
    @Query("""
        SELECT a
        FROM Apply a
        JOIN FETCH a.user u
        JOIN FETCH a.project p
        WHERE p.projectPk = :projectPk
        AND a.status <> 'CANCEL'
        ORDER BY a.createdAt DESC
    """)
    List<Apply> findAllByProjectPk(@Param("projectPk") Long projectPk);


    @Query("SELECT new com.S1_K4.ForkMe_BE.modules.apply.dto.MyApplyListResponseDto(" +
            "a.applyPk, a.content, a.status, a.createdAt, a.updatedAt, a.user.userPk,a.project.projectPk,a.projectPosition.position.positionPk, a.projectPosition.position.positionName) " +
            "FROM Apply a " +
            "WHERE a.user.userPk = :userPk AND a.deletedYN = 'N' AND a.status IN(:stateList)")
    List<MyApplyListResponseDto> findApplyByUserPkInState(Long userPk, List<String> stateList) ;


    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM Apply a WHERE a.user = (:user)")
    void deleteApplyByUserInBulk(User user);

    @Query("SELECT a FROM Apply a WHERE a.project.projectPk IN (:projectPkList)")
    List<Apply> findAllByProjectPkIn(List<Long> projectPkList);
}
