package com.S1_K4.ForkMe_BE.modules.on_project.schedule.service;

import com.S1_K4.ForkMe_BE.modules.on_project.schedule.dto.ScheduleCreateRequest;
import com.S1_K4.ForkMe_BE.modules.on_project.schedule.dto.ScheduleResponse;
import com.S1_K4.ForkMe_BE.modules.on_project.schedule.entity.Schedule;
import com.S1_K4.ForkMe_BE.modules.on_project.schedule.entity.ScheduleMention;
import com.S1_K4.ForkMe_BE.modules.on_project.schedule.repository.ScheduleRepository;
import com.S1_K4.ForkMe_BE.modules.project.entity.Project;
import com.S1_K4.ForkMe_BE.modules.project.repository.ProjectMemberRepository;
import com.S1_K4.ForkMe_BE.modules.project.repository.ProjectRepository;
import com.S1_K4.ForkMe_BE.modules.user.entity.User;
import com.S1_K4.ForkMe_BE.modules.user.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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
    private final ProjectMemberRepository projectMemberRepository;


    // 프로젝트 일정 조회
    public List<ScheduleResponse> getSchedulesByProject(Long projectPk, Long userPk) {

        Project project = projectRepository.findById(projectPk)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다."));

        List<Schedule> schedules = scheduleRepository.findAllByProject_ProjectPk(projectPk);

        User user = userRepository.findById(userPk)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (!projectMemberRepository.existsByProject_ProjectPkAndUser_UserPk(projectPk, userPk)) {
            throw new AccessDeniedException("해당 프로젝트의 멤버만 일정을 조회할 수 있습니다.");
        }



        return schedules.stream().map(schedule -> {
            List<Long> mentionedUserIds = schedule.getScheduleMentions().stream()
                    .map(mention -> mention.getUser().getUserPk())
                    .collect(Collectors.toList());

            return ScheduleResponse.builder()
                    .schedulePk(schedule.getSchedulePk())
                    .title(schedule.getTitle())
                    .start(schedule.getStartDate())
                    .end(schedule.getEndDate())
                    .userPk(userPk)
                    .scheduleMentionPk(mentionedUserIds)
                    .build();
        }).collect(Collectors.toList());
    }

    /**
     * 일정 저장 + 멘션 정보 저장 예시
     */
    @Transactional
    public ScheduleResponse createSchedule(ScheduleCreateRequest dto, Long projectPk, Long userPk) {
        Project project = projectRepository.findById(projectPk)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다."));

        User writer = userRepository.findById(userPk)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Schedule schedule = Schedule.create(
                dto.getTitle(),
                dto.getStart(),
                dto.getEnd(),
                project,
                writer
        );

        //멘션(멤버들) 생성
        if (dto.getScheduleMentionPk() != null) {
            Set<Long> uniqueUserIds = new HashSet<>();
            for (Long userId : dto.getScheduleMentionPk()) {
                // 중복 체크
                if (!uniqueUserIds.add(userId)) {
                    System.out.println("중복된 멘션 대상 id 가 있습니다.");
                    throw new IllegalArgumentException("중복된 멘션 대상 ID가 있습니다. ID: " + userId);
                }

                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new IllegalArgumentException("멘션 대상 사용자를 찾을 수 없습니다. ID: " + userId));

                // 멘션된 사용자가 해당 프로젝트의 멤버인지 확인
                if (!projectMemberRepository.existsByProject_ProjectPkAndUser_UserPk(projectPk, userId)) {
                    throw new IllegalArgumentException("멘션 대상 사용자는 해당 프로젝트의 멤버여야 합니다. ID: " + userId);
                }

                ScheduleMention mention = ScheduleMention.createMention(schedule, user);
                schedule.addScheduleMention(mention);
            }
        }

        Schedule savedSchedule = scheduleRepository.save(schedule);
        System.out.println("Saved schedulePk: " + savedSchedule.getSchedulePk());
        System.out.println("Mentions: " + savedSchedule.getScheduleMentions().size());

        List<Long> scheduleMentionPks = savedSchedule.getScheduleMentions().stream()
                .map(mention -> mention.getUser().getUserPk())
                .collect(Collectors.toList());

        return ScheduleResponse.builder()
                .schedulePk(savedSchedule.getSchedulePk())
                .title(savedSchedule.getTitle())
                .start(savedSchedule.getStartDate())
                .end(savedSchedule.getEndDate())
                .userPk(userPk)
                .scheduleMentionPk(scheduleMentionPks)
                .build();
    }

    @Transactional
    public ScheduleResponse updateSchedule(Long schedulePk, ScheduleCreateRequest dto,Long projectPk, Long userPk) throws AccessDeniedException {
        Schedule schedule = scheduleRepository.findById(schedulePk)
                .orElseThrow(() -> new IllegalArgumentException("일정을 찾을 수 없습니다. ID: " + schedulePk));

        Project project = projectRepository.findById(projectPk)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다."));

        User writer = userRepository.findById(userPk)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));



        schedule.setTitle(dto.getTitle());
        schedule.setStartDate(dto.getStart());
        schedule.setEndDate(dto.getEnd());

        List<Long> newMentionUserIds = Optional.ofNullable(dto.getScheduleMentionPk()).orElse(Collections.emptyList());

        // 중복 멘션 체크
        Set<Long> uniqueCheck = new HashSet<>();
        for (Long id : newMentionUserIds) {
            if (!uniqueCheck.add(id)) {
                throw new IllegalArgumentException("중복된 멘션 대상 ID가 있습니다. ID: " + id);
            }
        }

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

            // 멘션된 사용자가 해당 프로젝트의 멤버인지 확인
            if (!projectMemberRepository.existsByProject_ProjectPkAndUser_UserPk(projectPk, userId)) {
                throw new IllegalArgumentException("멘션 대상 사용자는 해당 프로젝트의 멤버여야 합니다. ID: " + userId);
            }
            ScheduleMention mention = ScheduleMention.createMention(schedule, user);
            schedule.addScheduleMention(mention);
        });

        Schedule saved = scheduleRepository.save(schedule);

        List<Long> mentionUserIds = saved.getScheduleMentions().stream()
                .map(m -> m.getUser().getUserPk())
                .collect(Collectors.toList());

        return ScheduleResponse.builder()
                .schedulePk(saved.getSchedulePk())
                .title(saved.getTitle())
                .start(saved.getStartDate())
                .end(saved.getEndDate())
                .userPk(userPk)
                .scheduleMentionPk(mentionUserIds)
                .build();
    }


    @Transactional
    public void deleteSchedule(Long schedulePk, Long userPk) {
        Schedule schedule = scheduleRepository.findById(schedulePk)
                .orElseThrow(() -> new IllegalArgumentException("일정을 찾을 수 없습니다."));

        User writer = userRepository.findById(userPk)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if(!schedule.getUser().getUserPk().equals(userPk)){
            throw new AccessDeniedException("일정 작성자가 아닙니다.");
        }

        scheduleRepository.deleteById(schedulePk);
    }

}