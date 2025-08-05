package com.S1_K4.ForkMe_BE.modules.project.repository;

import com.S1_K4.ForkMe_BE.modules.project.entity.Project;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.project.repository
 * @fileName : ProjectRepository
 * @date : 2025-08-05
 * @description : 프로젝트 리포지토리
 */
@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {


    @Query("""
    SELECT p FROM Project p
    JOIN FETCH p.user
    LEFT JOIN FETCH p.projectProfile
    WHERE p.projectPk = :projectPk
    """)
    Optional<Project> findWithProfileAndUserByProjectPk(@Param("projectPk") Long projectPk);
}