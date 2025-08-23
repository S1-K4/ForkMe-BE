package com.S1_K4.ForkMe_BE.modules.on_project.schedule.repository;

import com.S1_K4.ForkMe_BE.modules.on_project.schedule.entity.ScheduleMention;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author : 김송이
 * @packageName : com.S1_K4.ForkMe_BE.modules.on_project.schedule.repository
 * @fileName : ScheduleMentionRepository
 * @date : 2025-08-10
 * @description : 일정참여자 repository 입니다.
 */

@Repository
public interface ScheduleMentionRepository extends JpaRepository<ScheduleMention,Long> {
    List<ScheduleMention> findAllBySchedule_SchedulePk(Long schedulePk);
}
