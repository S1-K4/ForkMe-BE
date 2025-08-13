package com.S1_K4.ForkMe_BE.modules.chatting.service;

import com.S1_K4.ForkMe_BE.modules.chatting.dto.ChattingMessageDto;

import java.util.List;

/**
 * @author : 김남이
 * @packageName : com.S1_K4.ForkMe_BE.modules.chatting.service
 * @fileName : ChattingHistoryService
 * @date : 2025-08-07
 * @description : 이전 채팅 내역 관련 서비스 선언
 */
public interface ChattingHistoryService {

    List<ChattingMessageDto> getChattingHistory(Long chattingRoomPk, Long userPk);
}
