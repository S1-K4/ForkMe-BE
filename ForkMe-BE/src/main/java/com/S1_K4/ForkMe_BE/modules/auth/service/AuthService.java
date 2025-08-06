package com.S1_K4.ForkMe_BE.modules.auth.service;

import com.S1_K4.ForkMe_BE.modules.auth.dto.TokenRefreshResponseDto;

/**
 * @author : 김종국
 * @packageName : com.S1_K4.ForkMe_BE.modules.auth.service
 * @fileName : AuthService
 * @date : 2025-08-04
 * @description : Auth 서비스 인터페이스
 */
public interface AuthService {

    // 로그아웃
    void logout(String refreshToken);

    // Access Token 갱신
    TokenRefreshResponseDto reissueToken(String refreshToken);

}
