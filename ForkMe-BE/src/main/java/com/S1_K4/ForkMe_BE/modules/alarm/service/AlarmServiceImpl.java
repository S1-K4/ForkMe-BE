package com.S1_K4.ForkMe_BE.modules.alarm.service;

import com.S1_K4.ForkMe_BE.global.common.common_enum.Yn;
import com.S1_K4.ForkMe_BE.global.common.redis.RedisPublisher;
import com.S1_K4.ForkMe_BE.modules.alarm.dto.AlarmMessageRequest;
import com.S1_K4.ForkMe_BE.modules.alarm.mongo_document.AlarmMessageDocument;
import com.S1_K4.ForkMe_BE.modules.alarm.repository.AlarmMessageMongoRepository;
import com.S1_K4.ForkMe_BE.modules.apply.entity.Apply;
import com.S1_K4.ForkMe_BE.modules.chatting.dto.ChattingUserDto;
import com.S1_K4.ForkMe_BE.modules.chatting.entity.ChattingParticipant;
import com.S1_K4.ForkMe_BE.modules.chatting.entity.ChattingRoom;
import com.S1_K4.ForkMe_BE.modules.chatting.repository.ChattingRoomRepository;
import com.S1_K4.ForkMe_BE.modules.chatting.service.ChattingService;
import com.S1_K4.ForkMe_BE.modules.project.entity.Project;
import com.S1_K4.ForkMe_BE.modules.project.entity.ProjectMember;
import com.S1_K4.ForkMe_BE.modules.project.repository.ProjectMemberRepository;
import com.S1_K4.ForkMe_BE.modules.project.repository.ProjectRepository;
import com.S1_K4.ForkMe_BE.modules.user.entity.User;
import com.mongodb.client.result.UpdateResult;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    private final MongoTemplate mongoTemplate;
    private final RedisPublisher redisPublisher;
    private final ProjectMemberRepository projectMemberRepository;
    private final AlarmMessageMongoRepository alarmMessageMongoRepository;
    private final ProjectRepository projectRepository;
    private final ChattingRoomRepository chattingRoomRepository;

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
                .userPk(applicantPk)
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

    @Override
    public void alarmChattingMessageToMember(
            Long chattingRoomPk,
            User senderUser,
            LocalDateTime now,
            List<ChattingUserDto> participantsDetails){

        // fetch join 으로 다시 조회
        ChattingRoom chattingRoom = chattingRoomRepository.findWithParticipantsById(chattingRoomPk)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));

        Long senderUserPk = senderUser.getUserPk();

        Project project = projectRepository.findById(chattingRoom.getProjectPk().getProjectPk())
                .orElseThrow(() -> new IllegalStateException("해당하는 프로젝트가 없습니다."));

        List<ChattingParticipant> chattingParticipants = chattingRoom.getChattingParticipants();

        // DB 기준 대상 유저 (본인 제외, 중복 제거)
        List<Long> candidateUserPks = chattingParticipants.stream()
                .map(p -> p.getUserPk().getUserPk())
                .filter(pk -> !pk.equals(senderUserPk))
                .distinct()
                .toList();


        // 현재 방에 접속중인 유저 목록을 DTO 로 가져와서 online=true 인 userPk 집합을 만든다
        Set<Long> onlineUserPks = participantsDetails.stream()
                .filter(dto -> Boolean.TRUE.equals(dto.isOnline())) // ChattingUserDto#online 반환 타입에 맞게 조정
                .map(dto -> dto.getUserPk())
                .collect(Collectors.toSet());


        // 실제 알림을 보낼 대상: DB 후보 - (현재 접속중인 유저들)
        List<Long> targetUserPks = candidateUserPks.stream()
                .filter(pk -> !onlineUserPks.contains(pk))
                .toList();


        // 알림 생성/업서트 수행
        // 각 대상 유저에 대해 upsert 수행 (없으면 생성 + Redis 발행, 있으면 무시)
        for (Long targetUserPk : targetUserPks) {
            upsertUnreadChatAlarmOnce(targetUserPk, project.getProjectPk(), project.getProjectTitle(), now);
        }

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


    /** 헬퍼 메서드 **/
    private void upsertUnreadChatAlarmOnce(Long userPk, Long projectPk, String projectTitle, LocalDateTime now) {
        Query q = new Query(Criteria.where("userPk").is(userPk)
                .and("alarmType").is("CHAT")
                .and("referenceId").is(projectPk)
                .and("readYn").is(Yn.N)
                .and("deletedYn").is(Yn.N));

        Update u = new Update()
                .setOnInsert("userPk", userPk)
                .setOnInsert("alarmType", "CHAT")
                .setOnInsert("referenceId", projectPk)
                .setOnInsert("readYn", Yn.N)
                .setOnInsert("deletedYn", Yn.N)
                .setOnInsert("createdAt", now)
                .setOnInsert("alarmContent", "[" + projectTitle + "] 에 새 메시지가 있습니다.");

        UpdateResult res = mongoTemplate.upsert(q, u, AlarmMessageDocument.class);

        // new insert 된 경우에만 Redis 발행 (중복이 이미 있으면 발행하지 않음)
        if (res.getUpsertedId() != null) {
            AlarmMessageRequest alarm = AlarmMessageRequest.builder()
                    .userPk(userPk)
                    .alarmContent("[" + projectTitle + "] 에 새 메시지가 있습니다.")
                    .alarmType("CHAT")
                    .referenceId(projectPk)   // projectPk를 referenceId로 저장
                    .createdAt(now)
                    .build();

            redisPublisher.publishAlarm(userPk, alarm);
        }
    }
}
