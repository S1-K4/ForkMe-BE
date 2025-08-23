package com.S1_K4.ForkMe_BE.modules.alarm.controller;

import com.S1_K4.ForkMe_BE.global.exception.ApiResponse;
import com.S1_K4.ForkMe_BE.modules.alarm.dto.AlarmListResponse;
import com.S1_K4.ForkMe_BE.modules.alarm.service.AlarmHistoryService;
import com.S1_K4.ForkMe_BE.modules.auth.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : 김남이
 * @packageName : com.S1_K4.ForkMe_BE.modules.alarm.controller
 * @fileName : AlarmHistoryController
 * @date : 2025-08-20
 * @description : 이전 알람 내역을 불러오기 위한 컨트롤러 입니다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/alarm")
public class AlarmHistoryController {

    private final AlarmHistoryService alarmHistoryService;

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<AlarmListResponse>> getAlarms(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

        Long userPk = userDetails.getUserPk();
        AlarmListResponse AlarmResponse = alarmHistoryService.getUserAlarms(userPk, page, size);
        return ResponseEntity.ok(ApiResponse.success(AlarmResponse, "알림 리스트 조회 성공"));
    }

}
