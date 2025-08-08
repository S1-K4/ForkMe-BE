package com.S1_K4.ForkMe_BE.modules.chatting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * @author : 김남이
 * @packageName : com.S1_K4.ForkMe_BE.modules.chatting.dto
 * @fileName : ChattingUserDto
 * @date : 2025-08-07
 * @description : 채팅방 입/퇴장 시 유저 정보를 보내주는 dto
 */
@Getter
@AllArgsConstructor
@Builder
public class ChattingUserDto {

    private Long userPk;
    private String nickName;
    private boolean leader; // ✅ 리더 여부 필드 추가
}
