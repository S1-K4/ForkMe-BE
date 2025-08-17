package com.S1_K4.ForkMe_BE.modules.on_project.schedule.service;

import com.S1_K4.ForkMe_BE.modules.on_project.schedule.dto.ScheduleCreateRequest;
import com.S1_K4.ForkMe_BE.modules.on_project.schedule.dto.ScheduleResponse;

import java.nio.file.AccessDeniedException;
import java.util.List;

/**
 * @author : 김송이
 * @packageName : com.S1_K4.ForkMe_BE.modules.on_project.schedule.service
 * @fileName : ScheduleService
 * @date : 2025-08-10
 * @description : 일정 관리 service 입니다.
 */
public interface ScheduleService {

    List<ScheduleResponse> getSchedulesByProject(Long projectPk, Long userPk);
    ScheduleResponse createSchedule(ScheduleCreateRequest dto, Long projectPk, Long userPk);
    ScheduleResponse updateSchedule(Long schedulePk, ScheduleCreateRequest dto, Long projectPk, Long userPk);
    void deleteSchedule(Long schedulePk, Long userPk);
}
