package com.S1_K4.ForkMe_BE.modules.alarm.controller;

import com.S1_K4.ForkMe_BE.global.exception.ApiResponse;
import com.S1_K4.ForkMe_BE.modules.alarm.service.AlarmService;
import com.S1_K4.ForkMe_BE.modules.auth.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * @author : 김남이
 * @packageName : com.S1_K4.ForkMe_BE.modules.alarm.controller
 * @fileName : AlarmController
 * @date : 2025-08-20
 * @description : 알람의 현재 상태와 관련된 컨트롤러 입니다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/alarm/")
public class AlarmController {

    private  final AlarmService alarmService;

    @DeleteMapping("/{alarmPk}")
    public ResponseEntity<ApiResponse<String>> removeAlarm(
            @PathVariable(value = "alarmPk") String alarmPk,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

        //로그인 유저 검증
        Long userPk = userDetails.getUserPk();
        alarmService.removeAlarm(alarmPk); // 내부에서 검증/예외 처리

        return ResponseEntity.ok(ApiResponse.success(alarmPk, "알람 단건 삭제 완료"));
    }

    @DeleteMapping("/all")
    public ResponseEntity<ApiResponse<String>> removeAllAlarms(@AuthenticationPrincipal CustomUserDetails userDetails) {

        //로그인 유저 검증
        Long userPk = userDetails.getUserPk();

        alarmService.removeAllAlarms(userPk);

        return ResponseEntity.ok(ApiResponse.success("현재까지 알람 모두 지우기 완료"));
    }

    @PostMapping("/read")
    public ResponseEntity<ApiResponse<String>> readAllAlarms(@AuthenticationPrincipal CustomUserDetails userDetails) {

        //로그인 유저 검증
        Long userPk = userDetails.getUserPk();

        alarmService.readAllAlarms(userPk);

        return ResponseEntity.ok(ApiResponse.success("현재까지 알람 모두 읽기 완료"));
    }

}
