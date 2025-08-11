package com.S1_K4.ForkMe_BE.modules.on_project.schedule.controller;

import com.S1_K4.ForkMe_BE.modules.on_project.schedule.dto.ScheduleCreateRequest;
import com.S1_K4.ForkMe_BE.modules.on_project.schedule.dto.ScheduleResponse;
import com.S1_K4.ForkMe_BE.modules.on_project.schedule.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<ScheduleResponse>> getSchedules(@PathVariable Long projectPk) {
        List<ScheduleResponse> schedules = scheduleService.getSchedulesByProject(projectPk);
        return ResponseEntity.ok(schedules);
    }


    @PostMapping
    public ResponseEntity<ScheduleResponse> createSchedule(@RequestBody ScheduleCreateRequest dto) {
        log.info("📌 일정 생성 요청 들어옴: {}", dto);
        ScheduleResponse created = scheduleService.createSchedule(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{schedulePk}")
    public ResponseEntity<ScheduleResponse> updateSchedule(
            @PathVariable("schedulePk") Long schedulePk,
            @RequestBody ScheduleCreateRequest dto) {

        ScheduleResponse updated = scheduleService.updateSchedule(schedulePk, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{schedulePk}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable("schedulePk") Long schedulePk) {
        scheduleService.deleteSchedule(schedulePk);
        return ResponseEntity.noContent().build();
    }

}