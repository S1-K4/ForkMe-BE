package com.S1_K4.ForkMe_BE.modules.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : 김종국
 * @packageName : com.S1_K4.ForkMe_BE.modules.auth.dto
 * @fileName : TokenRefreshResponseDto
 * @date : 2025-08-04
 * @description : 새로 발급한 Access Token을 담아 보낼 응답 DTO
 */
@Getter
@AllArgsConstructor
public class TokenRefreshResponseDto {
    private String accessToken;
}