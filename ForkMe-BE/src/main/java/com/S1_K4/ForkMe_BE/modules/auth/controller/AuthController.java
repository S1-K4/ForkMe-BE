package com.S1_K4.ForkMe_BE.modules.auth.controller;

import com.S1_K4.ForkMe_BE.modules.auth.dto.LogoutRequestDto;
import com.S1_K4.ForkMe_BE.modules.auth.dto.TokenRefreshRequestDto;
import com.S1_K4.ForkMe_BE.modules.auth.dto.TokenRefreshResponseDto;
import com.S1_K4.ForkMe_BE.modules.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author : 김종국
 * @packageName : com.S1_K4.ForkMe_BE.modules.auth.controller
 * @fileName : AuthController
 * @date : 2025-08-04
 * @description : Auth 컨트롤러
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody LogoutRequestDto request){
        authService.logout(request.getRefreshToken());
        return ResponseEntity.ok("Success to logout.");
    }

    @PostMapping("/reissue")
    public ResponseEntity<TokenRefreshResponseDto> reissueToken(@RequestBody TokenRefreshRequestDto request){
        TokenRefreshResponseDto responseDto = authService.reissueToken(request.getRefreshToken());
        return ResponseEntity.ok(responseDto);
    }
}