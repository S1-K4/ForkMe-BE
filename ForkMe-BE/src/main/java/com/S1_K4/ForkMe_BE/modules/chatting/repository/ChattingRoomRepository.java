package com.S1_K4.ForkMe_BE.modules.chatting.repository;

import com.S1_K4.ForkMe_BE.modules.chatting.chatting_enum.RoomType;
import com.S1_K4.ForkMe_BE.modules.chatting.entity.ChattingRoom;
import com.S1_K4.ForkMe_BE.modules.project.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author : 김남이
 * @packageName : com.S1_K4.ForkMe_BE.modules.chatting.repository
 * @fileName : ChattingRoomRepository
 * @date : 2025-08-07
 * @description : 채팅방 정보 MySQL 저장용 레포지토리 입니다.
 */
@Repository
public interface ChattingRoomRepository extends JpaRepository<ChattingRoom, Long> {

    @Query("""
    SELECT r FROM ChattingRoom r
    JOIN r.chattingParticipants p
    WHERE r.projectPk.projectPk = :projectPk
      AND r.roomType = :roomType
      AND p.userPk.userPk IN (:user1, :user2)
    GROUP BY r
    HAVING COUNT(DISTINCT p.userPk.userPk) = 2 AND SIZE(r.chattingParticipants) = 2
""")
    Optional<ChattingRoom> findByProjectPkAndRoomTypeAndParticipants(
            @Param("projectPk") Long projectPk,
            @Param("roomType") RoomType roomType,
            @Param("user1") Long user1,
            @Param("user2") Long user2
    );

    // 프로젝트로 채팅방 찾기
    Optional<ChattingRoom> findByProjectPk(Project project);


}
