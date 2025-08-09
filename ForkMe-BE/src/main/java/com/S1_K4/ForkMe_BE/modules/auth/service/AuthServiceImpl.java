package com.S1_K4.ForkMe_BE.modules.auth.service;

import com.S1_K4.ForkMe_BE.modules.auth.dto.TokenRefreshResponseDto;
import com.S1_K4.ForkMe_BE.modules.auth.entity.Auth;
import com.S1_K4.ForkMe_BE.modules.auth.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/**
 * @author : 김종국
 * @packageName : com.S1_K4.ForkMe_BE.modules.auth.service
 * @fileName : AuthServiceImpl
 * @date : 2025-08-04
 * @description : Auth 서비스 클래스
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthRepository authRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void logout(String refreshToken) {
        log.info("logout / refreshToken = " + refreshToken + " /");
        Auth auth = authRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Refresh token is not found"));
        log.info("logout / authPk : " + auth.getAuthPk());
        authRepository.delete(auth);
    }


    @Override
    public TokenRefreshResponseDto reissueToken(String refreshToken) {
        log.info("reissueToken / refreshToken = " + refreshToken + " /");
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        Auth auth = authRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Refresh token is not found"));

        Authentication authentication = jwtTokenProvider.getAuthenticationByUser(auth.getUser());
        String newAccessToken = jwtTokenProvider.generateAccessToken(authentication);
        auth.setAccessToken(newAccessToken);
        log.info("reissueToken / newAccessToken = " + newAccessToken + " /");
        log.info("reissueToken / authPk : " + auth.getAuthPk());
        authRepository.save(auth);

        return new TokenRefreshResponseDto(newAccessToken);
    }

}