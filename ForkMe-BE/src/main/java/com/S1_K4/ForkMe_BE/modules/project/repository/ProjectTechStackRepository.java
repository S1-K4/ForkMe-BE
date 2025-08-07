package com.S1_K4.ForkMe_BE.modules.project.repository;

import com.S1_K4.ForkMe_BE.modules.project.entity.ProjectTechStack;
import com.S1_K4.ForkMe_BE.reference.stack.dto.TechStackResponseDTO;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.project.repository
 * @fileName : ProjectTechStackRepository
 * @date : 2025-08-05
 * @description : 프로젝트 관련 기술스택 레포지토리
 */
@Repository
public interface ProjectTechStackRepository extends JpaRepository<ProjectTechStack, Long> {

    @Query("SELECT new com.S1_K4.ForkMe_BE.reference.stack.dto.TechStackResponseDTO(t.techStack.techPk, t.techStack.techName) " +
            "FROM ProjectTechStack t " +
            "WHERE t.projectProfile.projectProfilePk = :profilePk")
    List<TechStackResponseDTO> findTechStacksByProfilePk(@Param("profilePk") Long profilePk);

    void deleteByProjectProfile_ProjectProfilePk(Long projectProfilePk);
}
