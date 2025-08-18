package com.S1_K4.ForkMe_BE.modules.project.repository;

import com.S1_K4.ForkMe_BE.modules.project.entity.ProjectPosition;
import com.S1_K4.ForkMe_BE.reference.position.dto.PositionResponseDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.project.repository
 * @fileName : ProjectPositionRepository
 * @date : 2025-08-05
 * @description : 프로젝트 관련 포지션 레포지토리
 */
@Repository
public interface ProjectPositionRepository extends JpaRepository<ProjectPosition,Long> {
    
    //특정 프로젝트 프로필에 연결된 포지션(positionPk, positionName) 조회
    @Query("SELECT new com.S1_K4.ForkMe_BE.reference.position.dto.PositionResponseDTO(p.position.positionPk, p.position.positionName) " +
            "FROM ProjectPosition p " +
            "WHERE p.projectProfile.projectProfilePk = :profilePk")
    List<PositionResponseDTO> findPositionsByProfilePk(@Param("profilePk") Long profilePk);

    //해당 프로필의 모든 ProjectPosition 삭제
    void deleteByProjectProfile_ProjectProfilePk(Long projectProfilePk);

    //pk 포지션 조회용
    List<ProjectPosition> findByProjectProfile_ProjectProfilePk(Long projectProfilePk);

    //특정 프로필에 연결된 포지션 PK목록 조회
    @Query("select pp.position.positionPk " +
            "from ProjectPosition pp " +
            "where pp.projectProfile.projectProfilePk = :profilePk")
    List<Long> findPositionPksByProfilePk(@Param("profilePk") Long profilePk);

    //해당 프로필에 연결된 포지션을 DTO로 바로 내려줌 -> projectPositionPk, projectPositionName
    @Query("""
        SELECT new com.S1_K4.ForkMe_BE.reference.position.dto.PositionResponseDTO(
            p.position.positionPk,
            p.position.positionName
        )
        FROM ProjectPosition p
        WHERE p.projectProfile.projectProfilePk = :profilePk
    """)
    List<PositionResponseDTO> findAllPositionDTOByProfilePk(@Param("profilePk") Long profilePk);

    //여러 프로필 PK에 연결된 ProjectPosition을 Position까지 fetch join으로 한 번에 조회 -> 목록 조회시 N+1 방지용
    @Query("""
        SELECT pp
        FROM ProjectPosition pp
        JOIN FETCH pp.position pos
        WHERE pp.projectProfile.projectProfilePk IN :profilePks
    """)
    List<ProjectPosition> findAllByProfilePksFetchPosition(@Param("profilePks") List<Long> profilePks);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM ProjectPosition pp WHERE pp.projectProfile.projectProfilePk IN (:projectProfilePkList)")
    void deleteByProjectProfile_ProjectProfilePkInBulk(List<Long> projectProfilePkList);
    
    //해당 프로필에 특정 포지션(positionPk)가 연결되어 있는지 확인
    Optional<ProjectPosition> findByProjectProfile_ProjectProfilePkAndPosition_PositionPk(
            Long projectProfilePk, Long positionPk);
}
