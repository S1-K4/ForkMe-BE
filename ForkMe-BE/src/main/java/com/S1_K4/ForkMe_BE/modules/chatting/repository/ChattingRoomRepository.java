package com.S1_K4.ForkMe_BE.modules.chatting.repository;

import com.S1_K4.ForkMe_BE.modules.chatting.chatting_enum.RoomType;
import com.S1_K4.ForkMe_BE.modules.chatting.entity.ChattingRoom;
import com.S1_K4.ForkMe_BE.modules.project.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
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

    // 프로젝트로 채팅방 찾기
    Optional<ChattingRoom> findByProjectPk(Project project);

    Optional<ChattingRoom> findByProjectPkAndRoomType(Project project, RoomType roomType);

    // 두명 모두 '현재 프로젝트 참여자' 인 기존 개인방
    @Query("""
        select r
        from ChattingRoom r
        join r.chattingParticipants p
        where r.projectPk.projectPk = :projectPk
          and r.roomType = :roomType
          and p.userPk.userPk in (:user1, :user2)
        group by r
        having count(distinct p.userPk.userPk) = 2
    """)
    Optional<ChattingRoom> findByProjectPkAndRoomTypeAndParticipants(
            @Param("projectPk") Long projectPk,
            @Param("roomType") RoomType roomType,
            @Param("user1") Long user1,
            @Param("user2") Long user2
    );


    // 두 사용자 모두 '메세지 기록' 이 있는 기존 개인방
    @Query("""
        select r
        from ChattingRoom r
        where r.projectPk.projectPk = :projectPk
          and r.roomType = :roomType
          and exists (
             select m1.chattingMessagePk           
             from ChattingMessage m1               
             where m1.chattingRoomPk = r
               and m1.userPk.userPk = :user1       
          )
          and exists (
             select m2.chattingMessagePk       
             from ChattingMessage m2
             where m2.chattingRoomPk = r
               and m2.userPk.userPk = :user2       
          )
    """)
    Optional<ChattingRoom> findPrivateRoomByHistory(
            @Param("projectPk") Long projectPk,
            @Param("roomType") RoomType roomType,
            @Param("user1") Long user1,
            @Param("user2") Long user2
    );

    //추가: 요청자는 '현재 참여자' + 상대는 '과거 메시지 기록'이 있는 기존 개인방 조회
    @Query("""
        select distinct r                       
        from ChattingRoom r
        join r.chattingParticipants p1      
        where r.projectPk.projectPk = :projectPk
          and r.roomType = :roomType
          and p1.userPk.userPk = :user1            
          and exists (
             select m2.chattingMessagePk            
             from ChattingMessage m2
             where m2.chattingRoomPk = r
               and m2.userPk.userPk = :user2     
          )
    """)
    Optional<ChattingRoom> findPrivateRoomByParticipantAndHistory(
            @Param("projectPk") Long projectPk,
            @Param("roomType") RoomType roomType,
            @Param("user1") Long user1,   // fromUserPk
            @Param("user2") Long user2    // toUserPk
    );


    // 프로젝트에 귀속되면서 내가 속한 개인 채팅방 리스트 조회
    @Query("""
    select distinct cr
    from ChattingRoom cr
    join ChattingParticipant cp on cp.chattingRoomPk = cr
    where cr.projectPk.projectPk = :projectPk
      and cr.roomType = :roomType
      and cp.userPk.userPk = :userPk
""")
    List<ChattingRoom> findMyPrivateRoomsInProject(
            @Param("projectPk") Long projectPk,
            @Param("roomType") RoomType roomType,
            @Param("userPk") Long userPk
    );

}

