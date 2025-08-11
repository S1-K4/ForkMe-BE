package com.S1_K4.ForkMe_BE.modules.project.repository;

import com.S1_K4.ForkMe_BE.modules.project.dto.CompletedProjectSummaryDto;
import com.S1_K4.ForkMe_BE.modules.project.dto.SideBarProjectDto;
import com.S1_K4.ForkMe_BE.modules.project.entity.Project;
import com.S1_K4.ForkMe_BE.modules.project.entity.ProjectMember;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
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

    //프로젝트 목록 조회
    @Query(
            value = "SELECT p FROM Project p JOIN FETCH p.user u JOIN FETCH p.projectProfile pf WHERE p.deletedYN = 'N'",
            countQuery = "SELECT COUNT(p) FROM Project p"
    )
    Page<Project> findProjectsWithUserAndProfile(Pageable pageable);

    @Query("SELECT p FROM Project p JOIN FETCH p.projectProfile WHERE p.projectPk = :projectPk AND p.deletedYN = 'N'")
    Optional<Project> findByIdWithProfile(@Param("projectPk") Long projectPk);

    @Query("SELECT new com.S1_K4.ForkMe_BE.modules.project.dto.SideBarProjectDto(p.projectPk, p.projectTitle, p.projectStatus) " +
            "FROM ProjectMember pm JOIN pm.project p " +
            "WHERE pm.user.userPk = :userPk AND p.projectStatus IN ('RECRUITING') AND p.deletedYN = 'N'")
    List<SideBarProjectDto> findRecruitingProjectsByUser(@Param("userPk") Long userPk);


    @Query("SELECT new com.S1_K4.ForkMe_BE.modules.project.dto.SideBarProjectDto(p.projectPk, p.projectTitle, p.projectStatus) " +
            "FROM ProjectMember pm JOIN pm.project p " +
            "WHERE pm.user.userPk = :userPk AND p.projectStatus IN ('IN_PROGRESS','ADDING') AND p.deletedYN = 'N'")
    List<SideBarProjectDto> findProgressProjectsByUser(@Param("userPk") Long userPk);


    @Query("SELECT new com.S1_K4.ForkMe_BE.modules.project.dto.CompletedProjectSummaryDto(" +
            "p.projectPk, p.projectTitle, " +
            "p.projectStartDate, p.projectEndDate, p.projectStatus," +
            "pp.projectProfilePk, pp.projectProfileTitle, pp.progressType) " +
            "FROM ProjectMember pm, ProjectProfile pp " + // 1. 필요한 엔티티들을 FROM 절에 나열
            "JOIN pm.project p " +
            "WHERE pp.project = p " + // 2. WHERE 절에서 ProjectProfile과 Project를 연결
            "AND pm.user.userPk = :userPk AND p.projectStatus = 'COMPLETED' AND p.deletedYN = 'N'")
    List<CompletedProjectSummaryDto> findCompletedProjectsByUserPk(@Param("userPk") Long userPk);

}
