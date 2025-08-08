package com.S1_K4.ForkMe_BE.modules.chatting.service;

import com.S1_K4.ForkMe_BE.modules.chatting.chatting_enum.RoomType;
import com.S1_K4.ForkMe_BE.modules.chatting.dto.ChattingMessageDto;
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

    ChattingRoom createChattingRoom(Project project, RoomType roomType, LocalDateTime now);

    void sendMessage(ChattingMessageDto chattingMessageDto);

    List<ChattingUserDto> getTeamChattingRoomParticipants(Long chattingRoomPk);

    void addChattingParticipant(ChattingRoom chattingRoom, User userPk, LocalDateTime now);

    void removeChattingParticipant(ChattingRoom chattingRoom, User user, LocalDateTime now);

    ChattingRoom getChattingRoom(Project projectPk);


}
