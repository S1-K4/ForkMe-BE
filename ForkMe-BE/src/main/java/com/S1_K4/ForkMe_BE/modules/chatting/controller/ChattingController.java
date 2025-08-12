package com.S1_K4.ForkMe_BE.modules.chatting.controller;

import com.S1_K4.ForkMe_BE.modules.chatting.chatting_enum.RoomType;
import com.S1_K4.ForkMe_BE.modules.chatting.dto.ChattingMessageDto;
import com.S1_K4.ForkMe_BE.modules.chatting.dto.ChattingUserDto;
import com.S1_K4.ForkMe_BE.modules.chatting.dto.PrivateChattingRoomResponse;
import com.S1_K4.ForkMe_BE.modules.chatting.entity.ChattingRoom;
import com.S1_K4.ForkMe_BE.modules.chatting.service.ChattingService;
import com.S1_K4.ForkMe_BE.modules.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

/**
 * @author : 김남이
 * @packageName : com.S1_K4.ForkMe_BE.modules.chatting.controller
 * @fileName : ChattingController
 * @date : 2025-08-07
 * @description : 실시간 채팅 담당 컨트롤러 입니다.
 */
@RestController
@RequestMapping("/api/chatting-room")
@RequiredArgsConstructor
public class ChattingController {

    private final ChattingService chattingService;

    /**
     * STOMP로 "/app/chat/message" 경로로 들어오는 메시지 처리
     */
    @MessageMapping("/chat/message")
    public void sendMessage(ChattingMessageDto chattingMessageDto) {
        chattingService.sendMessage(chattingMessageDto);
    }

    /**
     * 채팅방 참여자 리스트 조회
     */
    @GetMapping("/{chattingRoomPk}/participants")
    public List<ChattingUserDto> getChattingRoomParticipants(
            @PathVariable("chattingRoomPk") Long chattingRoomPk) {
        return chattingService.getChattingRoomParticipants(chattingRoomPk);
    }


    @GetMapping("/create")
    public PrivateChattingRoomResponse createPrivateChattingRoom(
            @RequestParam("projectPk") Long projectPk,
            @RequestParam("roomType") RoomType roomType,
            @RequestParam("fromUserPk") Long fromUserPk,
            @RequestParam("toUserPk") Long toUserPk

    ){
        //생성 시간 UTC
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);


        //채팅방 생성
        ChattingRoom chattingRoom = chattingService.createPrivateChattingRoom(projectPk, roomType, fromUserPk, toUserPk, now);

        //참여자 추가
        User fromUser = chattingService.addChattingParticipant(chattingRoom, fromUserPk, now);
        User toUser = chattingService.addChattingParticipant(chattingRoom, toUserPk, now);

        // 3. 직접 참여자 리스트 생성
        List<ChattingUserDto> participants = List.of(
                new ChattingUserDto(fromUser.getUserPk(), fromUser.getNickname(), false),
                new ChattingUserDto(toUser.getUserPk(), toUser.getNickname(), false)
        );

        // DTO 만들어서 반환
        return PrivateChattingRoomResponse.builder()
                .chattingRoomPk(chattingRoom.getChattingRoomPk())
                .roomType(roomType)
                .chattingRoomParticipants(participants)
                .build();

    }

}