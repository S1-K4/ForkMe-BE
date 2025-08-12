package com.S1_K4.ForkMe_BE.modules.user.controller;

import com.S1_K4.ForkMe_BE.modules.auth.dto.CustomUserDetails;
import com.S1_K4.ForkMe_BE.modules.user.dto.SideBarResponseDto;
import com.S1_K4.ForkMe_BE.modules.user.dto.UserInfoResponseDto;
import com.S1_K4.ForkMe_BE.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author : 김종국
 * @packageName : com.S1_K4.ForkMe_BE.modules.user.controller
 * @fileName : UserController
 * @date : 2025-08-06
 * @description : 유저 컨트롤러
 */

@RequestMapping("/api/user")
@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;

    // 유저 기본 정보 와 사이드바 정보 제공
    @GetMapping("/me")
    public ResponseEntity<UserInfoResponseDto> getMyProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        System.out.println("/me 접속");
        if (userDetails == null) {
            return ResponseEntity.ok(new UserInfoResponseDto());
        }
        Long userPk = userDetails.getUser().getUserPk();
        System.out.println("userPk : " + userPk);

        return ResponseEntity.ok(userService.getMyProfile(userPk));
    }


    // 사이드바
    @GetMapping("/me/sidebar")
    public ResponseEntity<SideBarResponseDto> getSidebarInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.ok(
                    // 비로그인은 빈 객체 전달
                    SideBarResponseDto.builder().preparingProjectList(null).workSpaceList(null).build()
            );
        }
        Long userPk = userDetails.getUser().getUserPk();
        return ResponseEntity.ok(userService.getSidebarInfo(userPk));
    }


    // 유저 기술 스택 저장
    @PostMapping("/me/updateStack")
    public ResponseEntity<String> updateUserTechStack(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody List<Long> techStackPkList) {

        userService.updateUserTechStack(userDetails.getUser(), techStackPkList);
        return ResponseEntity.ok("Success to save tech stack.");
    }


}