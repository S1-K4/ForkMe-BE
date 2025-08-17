package com.S1_K4.ForkMe_BE.modules.project.repository;

import com.S1_K4.ForkMe_BE.modules.project.dto.ProjectTechStackDto;
import com.S1_K4.ForkMe_BE.modules.project.entity.ProjectTechStack;
import com.S1_K4.ForkMe_BE.reference.stack.dto.TechStackResponseDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    //특정 프로젝트 프로필에 연결된 기술스택을 DTO로 조회 -> techPk, techName
    @Query("SELECT new com.S1_K4.ForkMe_BE.reference.stack.dto.TechStackResponseDTO(t.techStack.techPk, t.techStack.techName) " +
            "FROM ProjectTechStack t " +
            "WHERE t.projectProfile.projectProfilePk = :profilePk")
    List<TechStackResponseDTO> findTechStacksByProfilePk(@Param("profilePk") Long profilePk);

    //특정 프로필과 연결된 모든 기술스택(ProjectTechStack) 삭제
    void deleteByProjectProfile_ProjectProfilePk(Long projectProfilePk);
    
    //특정 프로필에 연결된 기술스택 엔티티 전부 조회
    @Query("""
        select pts 
        from ProjectTechStack pts
        join fetch pts.techStack
        where pts.projectProfile.projectProfilePk = :projectProfilePk
    """)
    List<ProjectTechStack> findByProjectProfile_ProjectProfilePk(@Param("projectProfilePk") Long projectProfilePk);

    //특정 프로젝트의 기술스택 PK만 조회
    @Query("""
        SELECT pts.techStack.techPk
        FROM ProjectTechStack pts
        WHERE pts.projectProfile.projectProfilePk = :projectProfilePk
    """)
    List<Long> findTechPksByProfilePk(@Param("projectProfilePk") Long projectProfilePk);

    @Query("SELECT new com.S1_K4.ForkMe_BE.modules.project.dto.ProjectTechStackDto(pts.projectProfile.projectProfilePk, pts.techStack.techPk, pts.techStack.techName)" +
            "FROM ProjectTechStack pts " +
            "WHERE pts.projectProfile.projectProfilePk IN (:profilePkList)")
    List<ProjectTechStackDto> findTechStacksByProfilePkIn(@Param("profilePkList") List<Long> profilePkList);

    //여러 프로필 PK에 연결된 ProjectTechStack을 TechStack까지 fetch join으로 한 번에 조회 → 목록 조회 시 N+1 방지용
    @Query("""
        SELECT pts
        FROM ProjectTechStack pts
        JOIN FETCH pts.techStack ts
        WHERE pts.projectProfile.projectProfilePk IN :profilePks
    """)
    List<ProjectTechStack> findAllByProfilePksFetchTech(@Param("profilePks") List<Long> profilePks);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM ProjectTechStack pt WHERE pt.projectProfile.projectProfilePk IN (:projectProfilePkList)")
    void deleteByProjectProfile_ProjectProfilePkInBulk(List<Long> projectProfilePkList);
}
