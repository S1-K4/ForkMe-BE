package com.S1_K4.ForkMe_BE.modules.auth;

import com.S1_K4.ForkMe_BE.modules.auth.service.JwtTokenProvider;
import com.S1_K4.ForkMe_BE.modules.auth.dto.CustomUserDetails;
import com.S1_K4.ForkMe_BE.modules.auth.entity.Auth;
import com.S1_K4.ForkMe_BE.modules.auth.repository.AuthRepository;
import com.S1_K4.ForkMe_BE.modules.user.entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

/**
 * @author : 김종국
 * @packageName : com.S1_K4.ForkMe_BE.modules.auth
 * @fileName : OAuth2AuthenticationSuccessHandler
 * @date : 2025-08-04
 * @description : 소셜 로그인 성공 시, JWT를 생성하고 클라이언트에게 전달하는 핸들러
 */

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthRepository authRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        String accessToken = jwtTokenProvider.generateAccessToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);

        Auth auth = authRepository.findByUser(user)
                .orElse(new Auth(user));
        auth.updateAccessToken(accessToken);
        auth.updateRefreshToken(refreshToken);
        authRepository.save(auth);

        String targetUrl = UriComponentsBuilder.fromUriString("http://localhost:8080/")
                .queryParam("accessToken", accessToken)
                .queryParam("refreshToken", refreshToken)
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}