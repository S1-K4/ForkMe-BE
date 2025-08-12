package com.S1_K4.ForkMe_BE.modules.project.repository;

import com.S1_K4.ForkMe_BE.modules.project.entity.ProjectMember;
import com.S1_K4.ForkMe_BE.modules.project.enums.IsLeader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
}