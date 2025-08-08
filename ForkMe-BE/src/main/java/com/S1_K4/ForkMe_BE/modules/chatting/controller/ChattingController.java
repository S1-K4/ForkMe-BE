package com.S1_K4.ForkMe_BE.modules.chatting.controller;

import com.S1_K4.ForkMe_BE.modules.chatting.dto.ChattingMessageDto;
import com.S1_K4.ForkMe_BE.modules.chatting.dto.ChattingUserDto;
import com.S1_K4.ForkMe_BE.modules.chatting.service.ChattingService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        return chattingService.getTeamChattingRoomParticipants(chattingRoomPk);
    }
}
