package com.S1_K4.ForkMe_BE.modules.auth.dto;

import lombok.Getter;

/**
 * @author : 김종국
 * @packageName : com.S1_K4.ForkMe_BE.modules.auth.dto
 * @fileName : LogoutRequestDto
 * @date : 2025-08-04
 * @description : 클라이언트가 Refresh Token을 담아 로그아웃 요청을 보낼 DTO
 */

@Getter
public class LogoutRequestDto {
    private String refreshToken;
}