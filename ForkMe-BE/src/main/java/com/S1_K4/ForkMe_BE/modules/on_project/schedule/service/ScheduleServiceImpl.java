package com.S1_K4.ForkMe_BE.modules.on_project.schedule.service;

import com.S1_K4.ForkMe_BE.modules.on_project.schedule.dto.ScheduleCreateRequest;
import com.S1_K4.ForkMe_BE.modules.on_project.schedule.dto.ScheduleResponse;
import com.S1_K4.ForkMe_BE.modules.on_project.schedule.entity.Schedule;
import com.S1_K4.ForkMe_BE.modules.on_project.schedule.entity.ScheduleMention;
import com.S1_K4.ForkMe_BE.modules.on_project.schedule.repository.ScheduleRepository;
import com.S1_K4.ForkMe_BE.modules.project.entity.Project;
import com.S1_K4.ForkMe_BE.modules.project.repository.ProjectRepository;
import com.S1_K4.ForkMe_BE.modules.user.entity.User;
import com.S1_K4.ForkMe_BE.modules.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : 김송이
 * @packageName : com.S1_K4.ForkMe_BE.modules.on_project.schedule.service
 * @fileName : ScheduleServiceImpl
 * @date : 2025-08-10
 * @description : 일정 관리 service입니다.
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService{
    private final ScheduleRepository scheduleRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    /**
     * 프로젝트별 일정 + 멘션된 사용자 ID 목록 조회
     */
    public List<ScheduleResponse> getSchedulesByProject(Long projectPk) {
        List<Schedule> schedules = scheduleRepository.findAllByProject_ProjectPk(projectPk);

        return schedules.stream().map(schedule -> {
            List<Long> mentionedUserIds = schedule.getScheduleMentions().stream()
                    .map(mention -> mention.getUser().getUserPk())
                    .collect(Collectors.toList());

            return ScheduleResponse.builder()
                    .id(schedule.getSchedulePk())
                    .title(schedule.getTitle())
                    .start(schedule.getStartDate())
                    .end(schedule.getEndDate())
                    .scheduleMentionPk(mentionedUserIds)
                    .build();
        }).collect(Collectors.toList());
    }

    /**
     * 일정 저장 + 멘션 정보 저장 예시
     */
    @Transactional
    public ScheduleResponse createSchedule(ScheduleCreateRequest dto) {
        Project project = projectRepository.findById(dto.getProjectPk())
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다."));

        User writer = userRepository.findById(dto.getUserPk())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Schedule schedule = Schedule.create(
                dto.getTitle(),
                dto.getStart(),
                dto.getEnd(),
                project,
                writer
        );

        // 멘션 생성
        if (dto.getScheduleMentionPk() != null) {
            dto.getScheduleMentionPk().forEach(userId -> {
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new IllegalArgumentException("멘션 대상 사용자를 찾을 수 없습니다. ID: " + userId));

                ScheduleMention mention = ScheduleMention.createMention(schedule, user);
                schedule.addScheduleMention(mention);  // ✅ 핵심
            });
        }

        Schedule savedSchedule = scheduleRepository.save(schedule);

        List<Long> scheduleMentionPks = savedSchedule.getScheduleMentions().stream()
                .map(mention -> mention.getUser().getUserPk())
                .collect(Collectors.toList());

        return ScheduleResponse.builder()
                .id(savedSchedule.getSchedulePk())
                .title(savedSchedule.getTitle())
                .start(savedSchedule.getStartDate())
                .end(savedSchedule.getEndDate())
                .scheduleMentionPk(scheduleMentionPks)
                .build();
    }

}