package com.S1_K4.ForkMe_BE.modules.on_project.schedule.repository;

import com.S1_K4.ForkMe_BE.modules.on_project.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author : 김송이
 * @packageName : com.S1_K4.ForkMe_BE.modules.on_project.schedule.repository
 * @fileName : ScheduleRepository
 * @date : 2025-08-10
 * @description : 일정관리 repository 입니다
 */

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findAllByProject_ProjectPk(Long projectPk);

}