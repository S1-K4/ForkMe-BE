package com.S1_K4.ForkMe_BE.modules.chatting.repository;

import com.S1_K4.ForkMe_BE.modules.chatting.chatting_enum.RoomType;
import com.S1_K4.ForkMe_BE.modules.chatting.entity.ChattingRoom;
import com.S1_K4.ForkMe_BE.modules.project.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
        JOIN r.chattingParticipants p1
        JOIN r.chattingParticipants p2
        WHERE r.projectPk.projectPk = :projectPk
          AND r.roomType = :roomType
          AND p1.userPk.userPk = :user1
          AND p2.userPk.userPk = :user2
    """)
    Optional<ChattingRoom> findByProjectPkAndRoomTypeAndParticipants(
            Long projectPk, RoomType roomType, Long user1, Long user2
    );

    // 프로젝트로 채팅방 찾기
    Optional<ChattingRoom> findByProjectPk(Project project);


}
