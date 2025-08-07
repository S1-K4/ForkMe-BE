package com.S1_K4.ForkMe_BE.modules.user.controller;

import com.S1_K4.ForkMe_BE.modules.auth.dto.CustomUserDetails;
import com.S1_K4.ForkMe_BE.modules.user.dto.SidebarResponseDto;
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

    // 사이드바
    @GetMapping("/me/sidebar")
    public ResponseEntity<SidebarResponseDto> getSidebarInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if(userDetails == null){
            return ResponseEntity.ok(
                    // 비로그인은 빈 객체 전달
                    SidebarResponseDto.builder().preparingProjectList(null).workSpaceList(null).build()
            );
        }
        return ResponseEntity.ok(userService.getSidebarInfo(userDetails.getUser()));
    }



    // 유저 기술 스택 저장
    @PostMapping("/me/stacks")
    public ResponseEntity<String> saveMyTechStack(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody List<Long> techStackPkList) {
        System.out.println(userDetails.toString());
        System.out.println(techStackPkList.toString());
        userService.updateUserTechStack(userDetails.getUser(), techStackPkList);
        return ResponseEntity.ok("Success to save tech stack.");
    }


}