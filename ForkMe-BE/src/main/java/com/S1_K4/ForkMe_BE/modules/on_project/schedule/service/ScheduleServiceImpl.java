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

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
                    .userPk(schedule.getUser().getUserPk())
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
                .userPk(savedSchedule.getUser().getUserPk())
                .scheduleMentionPk(scheduleMentionPks)
                .build();
    }

    @Transactional
    public ScheduleResponse updateSchedule(Long scheduleId, ScheduleCreateRequest dto) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("일정을 찾을 수 없습니다. ID: " + scheduleId));

        schedule.setTitle(dto.getTitle());
        schedule.setStartDate(dto.getStart());
        schedule.setEndDate(dto.getEnd());

        List<Long> newMentionUserIds = Optional.ofNullable(dto.getScheduleMentionPk()).orElse(Collections.emptyList());

        // 현재 멘션된 사용자 ID 목록
        List<ScheduleMention> existingMentions = new ArrayList<>(schedule.getScheduleMentions());
        List<Long> existingMentionUserIds = existingMentions.stream()
                .map(m -> m.getUser().getUserPk())
                .collect(Collectors.toList());

        // 추가할 멘션 ID
        List<Long> toAdd = newMentionUserIds.stream()
                .filter(id -> !existingMentionUserIds.contains(id))
                .collect(Collectors.toList());

        // 삭제할 멘션 ID
        List<Long> toRemove = existingMentionUserIds.stream()
                .filter(id -> !newMentionUserIds.contains(id))
                .collect(Collectors.toList());

        // 멘션 삭제 (양방향 관계 정리)
        toRemove.forEach(userId -> {
            existingMentions.stream()
                    .filter(m -> m.getUser().getUserPk().equals(userId))
                    .findFirst()
                    .ifPresent(mention -> {
                        schedule.removeScheduleMention(mention);  // 양방향 편의 메서드 호출
                    });
        });

        // 멘션 추가
        toAdd.forEach(userId -> {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("멘션 대상 사용자를 찾을 수 없습니다. ID: " + userId));
            ScheduleMention mention = ScheduleMention.createMention(schedule, user);
            schedule.addScheduleMention(mention);
        });

        Schedule saved = scheduleRepository.save(schedule);

        List<Long> mentionUserIds = saved.getScheduleMentions().stream()
                .map(m -> m.getUser().getUserPk())
                .collect(Collectors.toList());

        return ScheduleResponse.builder()
                .id(saved.getSchedulePk())
                .title(saved.getTitle())
                .start(saved.getStartDate())
                .end(saved.getEndDate())
                .userPk(saved.getUser().getUserPk())
                .scheduleMentionPk(mentionUserIds)
                .build();
    }


    @Transactional
    public void deleteSchedule(Long schedulePk) {
        Schedule schedule = scheduleRepository.findById(schedulePk)
                .orElseThrow(() -> new IllegalArgumentException("일정을 찾을 수 없습니다."));


        scheduleRepository.deleteById(schedulePk);
    }

}