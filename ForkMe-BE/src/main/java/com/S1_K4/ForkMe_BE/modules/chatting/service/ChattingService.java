package com.S1_K4.ForkMe_BE.modules.chatting.service;

import com.S1_K4.ForkMe_BE.modules.chatting.chatting_enum.RoomType;
import com.S1_K4.ForkMe_BE.modules.chatting.dto.ChattingMessageDto;
import com.S1_K4.ForkMe_BE.modules.chatting.dto.ChattingRoomResponse;
import com.S1_K4.ForkMe_BE.modules.chatting.dto.ChattingUserDto;
import com.S1_K4.ForkMe_BE.modules.chatting.entity.ChattingRoom;
import com.S1_K4.ForkMe_BE.modules.project.entity.Project;
import com.S1_K4.ForkMe_BE.modules.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author : 김남이
 * @packageName : com.S1_K4.ForkMe_BE.modules.chatting.service
 * @fileName : ChattingService
 * @date : 2025-08-07
 * @description : 실시간 채팅 관련 서비스 선언
 */
public interface ChattingService {

    ChattingRoom createTeamChattingRoom(Project project, RoomType roomType, LocalDateTime now);

    ChattingRoom createPrivateChattingRoom(
            Long project, RoomType roomType, Long fromUserPk, Long toUserPk, LocalDateTime now
    );

    void sendMessage(ChattingMessageDto chattingMessageDto);

    List<ChattingUserDto> getChattingRoomParticipants(Long chattingRoomPk);

    User addChattingParticipant(ChattingRoom chattingRoom, Long userPk, LocalDateTime now);

    void noticeJoinChattingRoom(ChattingRoom chattingRoom, User userPk, LocalDateTime now);

    void removeChattingParticipant(ChattingRoom chattingRoom, User user, LocalDateTime now);

    ChattingRoom getChattingRoom(Long projectPk, RoomType roomType);

    void performRemoveUserFromAllChattingRooms(Long projectPk, Long userPk, LocalDateTime now);

    boolean hasOtherUser(ChattingRoom chattingRoom, Long myUserPk);

    boolean isProjectMember(Long projectPk, Long userPk);

    //프로젝트 워크스페이스 내에서 유저가 속한 개인 채팅방 리스트 불러오기
    List<ChattingRoomResponse> getMyPrivateChattingRooms(Long projectPk, Long userPk);

    //프로젝트 삭제 시 연관된 모든 채팅방 삭제
    void softDeleteAllChattingRoomsByProject(Long projectPk, Long userPk);

    //프로젝트에 멤버 추가 시 기존 멤버와 자동으로 개인 채팅방 모두 생성
    void createAllPrivateRoomsForNewMember(Project project, User newMember, LocalDateTime now);

}

