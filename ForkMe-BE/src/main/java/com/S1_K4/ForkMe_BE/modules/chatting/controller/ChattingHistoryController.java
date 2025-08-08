package com.S1_K4.ForkMe_BE.modules.chatting.controller;

import com.S1_K4.ForkMe_BE.modules.chatting.dto.ChattingMessageDto;
import com.S1_K4.ForkMe_BE.modules.chatting.service.ChattingHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author : 김남이
 * @packageName : com.S1_K4.ForkMe_BE.modules.chatting.controller
 * @fileName : ChattingHistoryController
 * @date : 2025-08-07
 * @description : 이전 채팅 기록을 담당하는 컨트롤러 입니다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chatting-room")
public class ChattingHistoryController {

    private final ChattingHistoryService chattingHistoryService;

    // 특정 유저가 볼 수 있는 채팅방의 과거 메시지
    @GetMapping("/{chattingRoomPk}/messages/{userPk}")
    public List<ChattingMessageDto> getChattingHistory(
            @PathVariable("chattingRoomPk") Long chattingRoomPk,
            @PathVariable("userPk") Long userPk) {

        return chattingHistoryService.getChattingHistory(chattingRoomPk, userPk);
    }
}