package com.S1_K4.ForkMe_BE.modules.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author : 김종국
 * @packageName : com.S1_K4.ForkMe_BE.modules.user.dto
 * @fileName : UserSessionResponseDto
 * @date : 2025-08-12
 * @description : 유저 기본 정보 반환
 */

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoResponseDto {
    private Long userPk;
    private String email;
    private String nickname;
    private String profileUrl;
    private SideBarResponseDto sideBarResponseDto;
}