package com.S1_K4.ForkMe_BE.modules.chatting.repository;

import com.S1_K4.ForkMe_BE.modules.chatting.entity.ChattingParticipant;
import com.S1_K4.ForkMe_BE.modules.chatting.entity.ChattingRoom;
import com.S1_K4.ForkMe_BE.modules.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author : 김남이
 * @packageName : com.S1_K4.ForkMe_BE.modules.chatting.repository
 * @fileName : ChattingParticipantRepository
 * @date : 2025-08-07
 * @description : 채팅 참여자 MySQL 저장용 레포지토리 입니다.
 */
@Repository
public interface ChattingParticipantRepository extends JpaRepository<ChattingParticipant, Long> {

    //참여자 엔티티 조회
    Optional<ChattingParticipant> findByChattingRoomPkAndUserPk(ChattingRoom chattingRoom, User user);

    //채팅 방에 유저 단순 존재 여부 체크
    boolean existsByChattingRoomPkAndUserPk(ChattingRoom chattingRoomPk, User userPk);

    //채팅방에 해당하는 참여자 리스트 보기
    List<ChattingParticipant> findByChattingRoomPk(ChattingRoom chattingRoom);
}