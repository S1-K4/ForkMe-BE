package com.S1_K4.ForkMe_BE.modules.chatting.chatting_enum;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author : 김남이
 * @packageName : com.S1_K4.ForkMe_BE.modules.chatting.chatting_enum
 * @fileName : ChattingMessageType
 * @date : 2025-08-07
 * @description : 프론트 디자인 적용 시 구분하기 위해서 메세지 타입을 enum으로 지정합니다.
 */
@JsonFormat(shape = JsonFormat.Shape.STRING)
@Getter
@RequiredArgsConstructor
public enum ChattingMessageType {
    CHAT("일반 채팅 메세지"),
    JOIN("채팅방 입장 메세지"),
    LEAVE("채팅방 퇴장 메세지");

    private final String code;
}