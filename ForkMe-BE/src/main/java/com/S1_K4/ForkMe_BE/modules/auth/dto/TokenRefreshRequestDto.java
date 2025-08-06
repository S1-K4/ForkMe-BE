package com.S1_K4.ForkMe_BE.modules.auth.dto;

import lombok.Getter;

/**
 * @author : 김종국
 * @packageName : com.S1_K4.ForkMe_BE.modules.auth.dto
 * @fileName : TokenRefreshRequestDto
 * @date : 2025-08-04
 * @description : 클라리언트가 Refresh Token을 담아 보낼 요청 DTO
 */
@Getter
public class TokenRefreshRequestDto {
    private String refreshToken;
}