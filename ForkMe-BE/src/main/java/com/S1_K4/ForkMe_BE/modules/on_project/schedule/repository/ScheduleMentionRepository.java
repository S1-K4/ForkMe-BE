package com.S1_K4.ForkMe_BE.modules.on_project.schedule.repository;

import com.S1_K4.ForkMe_BE.modules.on_project.schedule.entity.ScheduleMention;

import java.util.List;

/**
 * @author : 김송이
 * @packageName : com.S1_K4.ForkMe_BE.modules.on_project.schedule.repository
 * @fileName : ScheduleMentionRepository
 * @date : 2025-08-10
 * @description : 일정참여자 repository 입니다.
 */
public interface ScheduleMentionRepository {
    List<ScheduleMention> findAllBySchedule_SchedulePk(Long schedulePk);
}
