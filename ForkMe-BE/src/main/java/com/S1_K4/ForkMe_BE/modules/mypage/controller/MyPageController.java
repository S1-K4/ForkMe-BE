package com.S1_K4.ForkMe_BE.modules.mypage.controller;

import com.S1_K4.ForkMe_BE.modules.auth.dto.CustomUserDetails;
import com.S1_K4.ForkMe_BE.modules.mypage.dto.MyPageResponseDto;
import com.S1_K4.ForkMe_BE.modules.mypage.service.MyPageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : 김종국
 * @packageName : com.S1_K4.ForkMe_BE.modules.mypage.controller
 * @fileName : MypageController
 * @date : 2025-08-07
 * @description : 마이페이지 컨트롤러
 */

@Slf4j
@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;

    @GetMapping("/getInfo")
    public ResponseEntity<MyPageResponseDto> getMyPageInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userPk = userDetails.getUser().getUserPk();
        log.info("getMyPageInfo / userPk = " + userPk + " /");
        MyPageResponseDto myPageResponseDto = myPageService.getMyPage(userPk);

        return ResponseEntity.ok(myPageResponseDto);
    }
}