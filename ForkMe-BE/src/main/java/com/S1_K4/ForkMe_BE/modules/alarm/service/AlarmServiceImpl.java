package com.S1_K4.ForkMe_BE.modules.alarm.service;

import com.S1_K4.ForkMe_BE.global.common.common_enum.Yn;
import com.S1_K4.ForkMe_BE.global.common.redis.RedisPublisher;
import com.S1_K4.ForkMe_BE.modules.alarm.dto.AlarmMessageRequest;
import com.S1_K4.ForkMe_BE.modules.alarm.mongo_document.AlarmMessageDocument;
import com.S1_K4.ForkMe_BE.modules.alarm.repository.AlarmMessageMongoRepository;
import com.S1_K4.ForkMe_BE.modules.apply.entity.Apply;
import com.S1_K4.ForkMe_BE.modules.project.entity.Project;
import com.S1_K4.ForkMe_BE.modules.project.entity.ProjectMember;
import com.S1_K4.ForkMe_BE.modules.project.repository.ProjectMemberRepository;
import com.S1_K4.ForkMe_BE.modules.user.entity.User;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

/**
 * @author : 김남이
 * @packageName : com.S1_K4.ForkMe_BE.modules.alarm.service
 * @fileName : AlarmServiceImple
 * @date : 2025-08-19
 * @description : 알람 기능 비즈니스 로직 구현
 */
@Service
@RequiredArgsConstructor
@Builder
public class AlarmServiceImpl implements  AlarmService{

    private final RedisPublisher redisPublisher;
    private final ProjectMemberRepository projectMemberRepository;
    private final AlarmMessageMongoRepository alarmMessageMongoRepository;

    @Override
    public void alarmApplyToLeader(User applicant, Project project, Apply apply) {
        // 팀장 찾기
        ProjectMember leader = projectMemberRepository.findLeaderByProjectPk(project)
                .orElseThrow(() -> new IllegalStateException("리더가 없습니다."));
        Long leaderPk = leader.getUser().getUserPk();

        //알람 시간 가져오기
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

        // 알림 메시지 조립
        AlarmMessageRequest alarm = AlarmMessageRequest.builder()
                .userPk(leaderPk)
                .alarmContent(applicant.getNickname() + "님이 [" + project.getProjectTitle() + "]에 지원했습니다.")
                .alarmType("APPLY")
                .referenceId(apply.getApplyPk())
                .createdAt(LocalDateTime.now())
                .build();

        //mongoDB 에 알림 메세지 저장
        alarmMessageMongoRepository.save(AlarmMessageRequest.toDocument(alarm));

        // Redis 발행
        redisPublisher.publishAlarm(leaderPk, alarm);
    }

    @Override
    public void alarmApplyToApplicant(User applicant, Project project, Apply apply, LocalDateTime now){

        Long applicantPk = applicant.getUserPk();

        // 알림 메시지 조립
        AlarmMessageRequest alarm = AlarmMessageRequest.builder()
                .userPk(applicant.getUserPk())
                .alarmContent("[" + project.getProjectTitle() + "] 에서 신청서를 수락했습니다.")
                .alarmType("APPLY")
                .referenceId(apply.getApplyPk())
                .createdAt(LocalDateTime.now())
                .build();

        //mongoDB 에 알림 메세지 저장
        alarmMessageMongoRepository.save(AlarmMessageRequest.toDocument(alarm));

        // Redis 발행
        redisPublisher.publishAlarm(applicantPk, alarm);

    }


    /** 알람 읽음 처리 **/
    @Override
    public void readAllAlarms(Long userPk){

        //삭제 안된 알람 중 아직 읽지 않은 알람 리스트 조회
        List<AlarmMessageDocument> alarms = alarmMessageMongoRepository.findByUserPkAndDeletedYnAndReadYn(userPk, Yn.N, Yn.N);

        for (AlarmMessageDocument alarm : alarms) {
            alarm.markAsRead(); // 읽음 처리
        }

        alarmMessageMongoRepository.saveAll(alarms); // 일괄 저장

    }


    /** 알람 단건 삭제 **/
    @Override
    public String removeAlarm(String alarmPk){

        AlarmMessageDocument alarmDocument = alarmMessageMongoRepository.findById(alarmPk)
                .orElseThrow(() -> new IllegalStateException("해당 알람이 존재하지 않습니다."));

        //알림 소프트 삭제
        alarmDocument.markAsDeleted();

        alarmMessageMongoRepository.save(alarmDocument);

        return alarmPk;
    }


    /** 알람 모두 삭제 **/
    @Override
    public void removeAllAlarms(Long userPk) {

        List<AlarmMessageDocument> alarms = alarmMessageMongoRepository.findByUserPkAndDeletedYn(userPk, Yn.N);

        for (AlarmMessageDocument alarm : alarms) {
            alarm.markAsDeleted(); // 소프트 삭제
        }

        alarmMessageMongoRepository.saveAll(alarms); // 일괄 저장

    }
}
