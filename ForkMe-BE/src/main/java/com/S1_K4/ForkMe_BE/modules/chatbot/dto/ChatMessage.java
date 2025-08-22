package com.S1_K4.ForkMe_BE.modules.chatbot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.chatbot.dto
 * @fileName : ChatMessage
 * @date : 2025-08-20
 * @description : 챗메세지 DTO
 */
//화면에 뿌릴 한줄 메시지
@Schema(description = "웹소켓/STOPM 채팅 메시지 DTO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    @Schema(description = "발신자 표시명 (예: '나', 'GPT-봇')")
    private String from;

    @Schema(description = "메시지 본문")
    private String message;
}