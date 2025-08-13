package com.S1_K4.ForkMe_BE.modules.project.repository;

import com.S1_K4.ForkMe_BE.modules.project.entity.Project;
import com.S1_K4.ForkMe_BE.modules.project.entity.ProjectMember;
import com.S1_K4.ForkMe_BE.modules.project.enums.IsLeader;
import com.S1_K4.ForkMe_BE.modules.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.project.repository
 * @fileName : ProjectMemberRepository
 * @date : 2025-08-07
 * @description : Project 참여인원 repository
 */
@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {
    void deleteByProject_ProjectPk(Long projectPk);

    //팀장 여부 확인
    boolean existsByProject_ProjectPkAndUser_UserPkAndIsLeader(
            Long projectPk, Long userPk, IsLeader isLeader
    );
    // 프로젝트 + 유저로 멤버 조회
    @Query("SELECT pm FROM ProjectMember pm WHERE pm.project = :project AND pm.user = :user")
    Optional<ProjectMember> findByProjectPkAndUserPk(@Param("project") Project project, @Param("user") User user);

    //해당 프로젝트에 속한 특정 사용자의 ProjectMember 엔티티를 조회
    Optional<ProjectMember> findByProject_ProjectPkAndUser_UserPk(Long projectPk, Long userPk);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        DELETE FROM ProjectMember pm
        WHERE pm.project.projectPk = :projectPk
          AND pm.user.userPk     = :targetUserPk
    """)
    int deleteByProjectPkAndTargetUserPk(@Param("projectPk") Long projectPk,
                                         @Param("targetUserPk") Long targetUserPk);

}