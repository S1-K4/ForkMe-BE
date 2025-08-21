package com.S1_K4.ForkMe_BE.modules.on_project.schedule.controller;

import com.S1_K4.ForkMe_BE.global.exception.ApiResponse;
import com.S1_K4.ForkMe_BE.modules.auth.dto.CustomUserDetails;
import com.S1_K4.ForkMe_BE.modules.on_project.schedule.dto.ScheduleCreateRequest;
import com.S1_K4.ForkMe_BE.modules.on_project.schedule.dto.ScheduleResponse;
import com.S1_K4.ForkMe_BE.modules.on_project.schedule.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author : 김송이
 * @packageName : com.S1_K4.ForkMe_BE.modules.on_project.schedule.controller
 * @fileName : ScheduleController
 * @date : 2025-08-10
 * @description : 일정 관리 contorller 입니다.
 */

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/schedules/project/{projectPk}")
@RestController
public class ScheduleController {

    private final ScheduleService scheduleService;


    // 일정 조회
    @GetMapping
    public ResponseEntity<List<ScheduleResponse>> getSchedules(@PathVariable Long projectPk,
                                                               @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userPk= userDetails.getUserPk();
        List<ScheduleResponse> schedules = scheduleService.getSchedulesByProject(projectPk, userPk);
        return ResponseEntity.ok(schedules);
    }


    // 일정 등록
    @PostMapping
    public ResponseEntity<ScheduleResponse> createSchedule(@RequestBody ScheduleCreateRequest dto,
                                                           @PathVariable("projectPk") Long projectPk,
                                                           @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userPk= userDetails.getUserPk();
        ScheduleResponse created = scheduleService.createSchedule(dto,projectPk, userPk);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // 일정 수정
    @PutMapping("/{schedulePk}")
    public ResponseEntity<ScheduleResponse> updateSchedule(
            @PathVariable("schedulePk") Long schedulePk,
            @RequestBody ScheduleCreateRequest dto,
            @PathVariable("projectPk") Long projectPk,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long userPk= userDetails.getUserPk();
        ScheduleResponse updated = scheduleService.updateSchedule(schedulePk, dto, projectPk, userPk);
        return ResponseEntity.ok(updated);
    }

    // 일정 삭제
    @DeleteMapping("/{schedulePk}")
    public ResponseEntity<ApiResponse<?>> deleteSchedule(@PathVariable("schedulePk") Long schedulePk,
                                                      @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userPk= userDetails.getUserPk();
        scheduleService.deleteSchedule(schedulePk, userPk);
        return ResponseEntity.ok(ApiResponse.success("일정 pk 번호 : "+ schedulePk, "일정 삭제 완료"));
    }

}