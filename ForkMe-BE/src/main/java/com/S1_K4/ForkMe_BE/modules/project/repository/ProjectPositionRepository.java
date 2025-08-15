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
    @Query("SELECT new com.S1_K4.ForkMe_BE.reference.position.dto.PositionResponseDTO(p.position.positionPk, p.position.positionName) " +
            "FROM ProjectPosition p " +
            "WHERE p.projectProfile.projectProfilePk = :profilePk")
    List<PositionResponseDTO> findPositionsByProfilePk(@Param("profilePk") Long profilePk);

    //포지션 삭제
    void deleteByProjectProfile_ProjectProfilePk(Long projectProfilePk);

    //pk 포지션 조회용
    List<ProjectPosition> findByProjectProfile_ProjectProfilePk(Long projectProfilePk);

    @Query("select pp.position.positionPk " +
            "from ProjectPosition pp " +
            "where pp.projectProfile.projectProfilePk = :profilePk")
    List<Long> findPositionPksByProfilePk(@Param("profilePk") Long profilePk);

    @Query("""
        SELECT new com.S1_K4.ForkMe_BE.reference.position.dto.PositionResponseDTO(
            p.projectPositionPk,
            p.position.positionName
        )
        FROM ProjectPosition p
        WHERE p.projectProfile.projectProfilePk = :profilePk
    """)
    List<PositionResponseDTO> findAllPositionDTOByProfilePk(@Param("profilePk") Long profilePk);

    @Query("""
        SELECT p
        FROM ProjectPosition p
        WHERE p.projectProfile.project.projectPk = :projectPk
          AND p.projectPositionPk = :projectPositionPk
    """)
    Optional<ProjectPosition> findByProjectPkAndProjectPositionPk(@Param("projectPk") Long projectPk,
                                                                  @Param("projectPositionPk") Long projectPositionPk);

    @Query("""
        SELECT p
        FROM ProjectPosition p
        WHERE p.projectProfile.projectProfilePk = :projectProfilePk
          AND p.position.positionPk = :positionPk
    """)
    Optional<ProjectPosition> findByProjectProfilePkAndPositionPk(
            @Param("projectProfilePk") Long projectProfilePk,
            @Param("positionPk") Long positionPk
    );

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM ProjectPosition pp WHERE pp.projectProfile.projectProfilePk IN (:projectProfilePkList)")
    void deleteByProjectProfile_ProjectProfilePkInBulk(List<Long> projectProfilePkList);

    Optional<ProjectPosition> findByProjectProfile_ProjectProfilePkAndPosition_PositionPk(
            Long projectProfilePk, Long positionPk);

}
