package com.S1_K4.ForkMe_BE.modules.project.repository;

import com.S1_K4.ForkMe_BE.modules.project.dto.CompletedProjectSummaryDto;
import com.S1_K4.ForkMe_BE.modules.project.dto.SideBarProjectDto;
import com.S1_K4.ForkMe_BE.modules.project.entity.Project;
import com.S1_K4.ForkMe_BE.modules.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
 * @fileName : ProjectRepository
 * @date : 2025-08-05
 * @description : 프로젝트 리포지토리
 */
@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    //특정 프로젝트(projectPk) 한건 + 속해있는 유저 + 프로필까지 조회(단건 조회)
    @Query("""
            SELECT p FROM Project p
            JOIN FETCH p.user
            LEFT JOIN FETCH p.projectProfile
            WHERE p.projectPk = :projectPk AND p.deletedYN = 'N'
            """)
    Optional<Project> findWithProfileAndUserByProjectPk(@Param("projectPk") Long projectPk);

    //프로젝트 목록 조회(유저, 프로필 즉시로딩 & 페이징 처리)
    @Query(
            value = "SELECT p FROM Project p JOIN FETCH p.user u JOIN FETCH p.projectProfile pf WHERE p.deletedYN = 'N'",
            countQuery = "SELECT COUNT(p) FROM Project p WHERE p.deletedYN='N'"
    )
    Page<Project> findProjectsWithUserAndProfile(Pageable pageable);

    //특정 프로젝트+프로필 조회(단건)
    @Query("""
        SELECT p FROM Project p 
        JOIN FETCH p.projectProfile 
        WHERE p.projectPk = :projectPk AND p.deletedYN = 'N'
    """)
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
            "p.projectPk, p.projectTitle, p.projectStartDate, p.projectEndDate, p.projectStatus," +
            "pp.projectProfilePk, pp.projectProfileTitle, pp.progressType) " +
            "FROM ProjectMember pm, ProjectProfile pp " +
            "JOIN pm.project p " +
            "WHERE pp.project = p " +
            "AND pm.user.userPk = :userPk AND p.projectStatus = 'COMPLETED' AND p.deletedYN = 'N'")
    List<CompletedProjectSummaryDto> findCompletedProjectsByUserPk(@Param("userPk") Long userPk);

    List<Project> findAllByUser(User user);


    @Modifying(clearAutomatically = true)
    @Query("UPDATE Project p SET p.deletedYN = 'Y' WHERE p.projectPk IN (:projectPkList)")
    void softDeleteByProjectPkInBulk(List<Long> projectPkList);
}
