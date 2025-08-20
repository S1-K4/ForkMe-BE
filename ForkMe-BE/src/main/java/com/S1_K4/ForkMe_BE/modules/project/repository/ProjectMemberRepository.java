package com.S1_K4.ForkMe_BE.modules.project.repository;

import com.S1_K4.ForkMe_BE.modules.project.dto.ProjectMemberCountDto;
import com.S1_K4.ForkMe_BE.modules.project.dto.ProjectMemberListDTO;
import com.S1_K4.ForkMe_BE.modules.project.entity.Project;
import com.S1_K4.ForkMe_BE.modules.project.entity.ProjectMember;
import com.S1_K4.ForkMe_BE.modules.project.enums.IsLeader;
import com.S1_K4.ForkMe_BE.modules.user.entity.User;
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
 * @fileName : ProjectMemberRepository
 * @date : 2025-08-07
 * @description : Project 참여인원 repository
 */
@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {
    //해당 projectPk를 가지는 ProjectMember 필드 삭제
    void deleteByProject_ProjectPk(Long projectPk);

    //해당 user가 특정 프로젝트(projectPk)의 팀장인지 확인
    boolean existsByProject_ProjectPkAndUser_UserPkAndIsLeader(
            Long projectPk, Long userPk, IsLeader isLeader
    );

    //특정 프로젝트에 참여한 유저 조회
    @Query("""
        select new com.S1_K4.ForkMe_BE.modules.project.dto.ProjectMemberListDTO(
            u.userPk,
            u.nickname,
            cast(pm.isLeader as string)
        )
        from ProjectMember pm
        join pm.user u
        where pm.project.projectPk = :projectPk
        order by 
            case when pm.isLeader = com.S1_K4.ForkMe_BE.modules.project.enums.IsLeader.LEADER then 0 else 1 end,
            u.nickname
        """)
    List<ProjectMemberListDTO> findMemberListByProjectPk(@Param("projectPk") Long projectPk);

    // 프로젝트 + 유저로 멤버 조회
    @Query("SELECT pm FROM ProjectMember pm WHERE pm.project = :project AND pm.user = :user")
    Optional<ProjectMember> findByProjectPkAndUserPk(@Param("project") Project project, @Param("user") User user);

    //해당 프로젝트에 속한 특정 사용자의 ProjectMember 엔티티를 조회 -> 즉, 해당 프로젝트에 사용자가 속해 있는지 확인
    Optional<ProjectMember> findByProject_ProjectPkAndUser_UserPk(Long projectPk, Long userPk);

    //특정 프로젝트에서 특정 사용자를 멤버 목록에서 제거 -> 강퇴
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        DELETE FROM ProjectMember pm
        WHERE pm.project.projectPk = :projectPk
          AND pm.user.userPk     = :targetUserPk
    """)
    int deleteByProjectPkAndTargetUserPk(@Param("projectPk") Long projectPk,
                                         @Param("targetUserPk") Long targetUserPk);


    // 프로젝트 pk 리스트로 조회
    @Query("SELECT new com.S1_K4.ForkMe_BE.modules.project.dto.ProjectMemberCountDto(pm.project.projectPk, COUNT(pm.projectMemberPk))"+
            "FROM ProjectMember pm " +
            "WHERE pm.project.projectPk IN (:projectPkList) " +
            "GROUP BY pm.project.projectPk")
    List<ProjectMemberCountDto> findProjectMemberCountByProjectPk(@Param("projectPkList") List<Long> projectPkList);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM ProjectMember pm WHERE pm.project.projectPk IN (:projectPkList)")
    void deleteByProject_ProjectPkInBulk(List<Long> projectPkList);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM ProjectMember pm WHERE pm.user = (:user)")
    void deleteByUserInBulk(User user);

    //특정 유저가 특정 프로젝트에 속해있는지(존재하는지) 확인
    boolean existsByProject_ProjectPkAndUser_UserPk(Long projectPk, Long userPk);


    // 특정 프로젝트의 리더 찾기
    @Query("SELECT pm FROM ProjectMember pm WHERE pm.project = :project AND pm.isLeader = 'LEADER'")
    Optional<ProjectMember> findLeaderByProjectPk(@Param("project") Project project);
}