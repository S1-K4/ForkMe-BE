package com.S1_K4.ForkMe_BE.modules.chatting.dto;

import com.S1_K4.ForkMe_BE.modules.chatting.chatting_enum.RoomType;
import lombok.*;

import java.util.List;

/**
 * @author : 김남이
 * @packageName : com.S1_K4.ForkMe_BE.modules.chatting.dto
 * @fileName : PrivateChattingRoomRequest
 * @date : 2025-08-11
 * @description : 개인 채팅방 생성 시 정보를 보여주는 포맷
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrivateChattingRoomResponse {

    private Long chattingRoomPk;
    private RoomType roomType;
    private List<ChattingUserDto> chattingRoomParticipants;
}

