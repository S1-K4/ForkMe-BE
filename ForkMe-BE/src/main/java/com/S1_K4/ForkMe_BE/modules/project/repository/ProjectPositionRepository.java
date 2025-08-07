package com.S1_K4.ForkMe_BE.modules.project.repository;

import com.S1_K4.ForkMe_BE.modules.project.entity.Project;
import com.S1_K4.ForkMe_BE.modules.project.entity.ProjectPosition;
import com.S1_K4.ForkMe_BE.modules.project.entity.ProjectProfile;
import com.S1_K4.ForkMe_BE.reference.position.dto.PositionResponseDTO;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

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

    void deleteByProjectProfile_ProjectProfilePk(Long projectProfilePk);

    List<ProjectPosition> findByProjectProfile(ProjectProfile projectProfile);
}
