package com.S1_K4.ForkMe_BE.modules.chatting.chatting_enum;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author : 김남이
 * @packageName : com.S1_K4.ForkMe_BE.modules.chatting.chatting_enum
 * @fileName : RoomType
 * @date : 2025-08-06
 * @description : 채팅방 타입을 지정하는 enum
 */
@Getter
@RequiredArgsConstructor
public enum RoomType {
    T("T"), //팀 채팅방
    P("P"); // 개인 채팅방
    private final String code;
}
