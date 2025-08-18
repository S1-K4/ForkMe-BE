package com.S1_K4.ForkMe_BE.modules.chatting.service;

import com.S1_K4.ForkMe_BE.global.common.redis.RedisPublisher;
import com.S1_K4.ForkMe_BE.modules.chatting.chatting_enum.ChattingMessageType;
import com.S1_K4.ForkMe_BE.modules.chatting.chatting_enum.RoomType;
import com.S1_K4.ForkMe_BE.modules.chatting.dto.ChattingMessageDto;
import com.S1_K4.ForkMe_BE.modules.chatting.dto.ChattingRoomResponse;
import com.S1_K4.ForkMe_BE.modules.chatting.dto.ChattingUserDto;
import com.S1_K4.ForkMe_BE.modules.chatting.entity.ChattingParticipant;
import com.S1_K4.ForkMe_BE.modules.chatting.entity.ChattingRoom;
import com.S1_K4.ForkMe_BE.modules.chatting.presence.service.ChattingPresenceService;
import com.S1_K4.ForkMe_BE.modules.chatting.repository.ChattingMessageMongoRepository;
import com.S1_K4.ForkMe_BE.modules.chatting.repository.ChattingMessageRepository;
import com.S1_K4.ForkMe_BE.modules.chatting.repository.ChattingParticipantRepository;
import com.S1_K4.ForkMe_BE.modules.chatting.repository.ChattingRoomRepository;
import com.S1_K4.ForkMe_BE.modules.project.entity.Project;
import com.S1_K4.ForkMe_BE.modules.project.enums.IsLeader;
import com.S1_K4.ForkMe_BE.modules.project.repository.ProjectMemberRepository;
import com.S1_K4.ForkMe_BE.modules.project.repository.ProjectRepository;
import com.S1_K4.ForkMe_BE.modules.user.entity.User;
import com.S1_K4.ForkMe_BE.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

/**
 * @author : 김남이
 * @packageName : com.S1_K4.ForkMe_BE.modules.chatting.service
 * @fileName : ChattingServiceImpl
 * @date : 2025-08-07
 * @description : 실시간 채팅 관련 서비스 구현
 */
@Service
@RequiredArgsConstructor
public class ChattingServiceImpl implements ChattingService{

    private final RedisPublisher redisPublisher;
    private final ChattingMessageRepository chattingMessageRepository;
    private final ChattingMessageMongoRepository chattingMessageMongoRepository;
    private final ChattingRoomRepository chattingRoomRepository;
    private final ChattingParticipantRepository chattingParticipantRepository;
    private final UserRepository userRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectRepository projectRepository;
    private final ChattingPresenceService chattingPresenceService;


    @Override
    public ChattingRoom createTeamChattingRoom(Project project, RoomType roomType, LocalDateTime now) {
        // 채팅방 생성
        ChattingRoom chattingRoom = ChattingRoom.create(project, roomType, now);
        chattingRoomRepository.save(chattingRoom);

        return chattingRoom;
    }

    @Override
    public ChattingRoom createPrivateChattingRoom(
            Long projectPk, RoomType roomType, Long fromUserPk, Long toUserPk, LocalDateTime now
    ){
        // 실제 DB 에서 한번 더 조회해도 좋음 (PK로 비교만 하려면 아래처럼도 가능)

        //프로젝트 존재 확인
        Project project = projectRepository.findById(projectPk)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 프로젝트입니다."));

        //유저 존재만 확인(멤버 검증은  '새로 만들 때만')
        userRepository.findById(fromUserPk)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
        userRepository.findById(toUserPk)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));


        //1. 채팅방 참여자 두명 모두 '현재 참여자' 인 경우 기존 방 반환
        ChattingRoom existingChattingRoom = chattingRoomRepository
                .findByProjectPkAndRoomTypeAndParticipants(projectPk, roomType, fromUserPk, toUserPk)
                .orElse(null);

        if (existingChattingRoom != null) {
            // 이미 있으면 그 방 리턴
            return existingChattingRoom;
        }

        // 2.요청자(fromUser)는 현재 '참여자' + 상대(toUser)는 과거 '메시지 기록' 이 있는 기존 방
        existingChattingRoom = chattingRoomRepository
                .findPrivateRoomByParticipantAndHistory(projectPk, roomType, fromUserPk, toUserPk) // ← 여기 추가
                .orElse(null);
        if (existingChattingRoom != null) return existingChattingRoom;


        /** 방을 생성하기 전 프로젝트 멤버인지 검사 **/

        // 둘 다 없으면 "생성 가능 조건" 체크: 두 사람 모두 현재 프로젝트 멤버여야 함
        validateProjectMember(project, fromUserPk);
        validateProjectMember(project, toUserPk);

        //존재하는 방 없으면 방 생성
        ChattingRoom newChattingRoom = ChattingRoom.create(project, roomType, now);
        chattingRoomRepository.save(newChattingRoom);

        return newChattingRoom;
    }

    @Override
    public ChattingRoom getChattingRoom(Long projectPk, RoomType roomType) {
        Project project = projectRepository.findById(projectPk)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트가 존재하지 않습니다."));

        return chattingRoomRepository.findByProjectPkAndRoomType(project, roomType)
                .orElseThrow(() -> new IllegalStateException("해당 타입의 채팅방이 존재하지 않습니다."));
    }


    @Override
    public void sendMessage(ChattingMessageDto chattingMessageDto) {

        //서버용 시간은 UTC 시간으로 포맷 맞추기
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

        // 1. 채팅방 조회
        ChattingRoom chattingRoom = chattingRoomRepository.findById(chattingMessageDto.getChattingRoomPk())
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));

        // 2. 방 종류 확인
        if (chattingRoom.getRoomType() != RoomType.T && chattingRoom.getRoomType() != RoomType.P) {
            throw new IllegalStateException("잘못된 채팅방 타입입니다.");
        }

        // 3. 유저 조회
        User user = userRepository.findById(chattingMessageDto.getUserPk())
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        // 4. 참여자 권한 검증
        chattingParticipantRepository.findByChattingRoomPkAndUserPk(chattingRoom, user)
                .orElseThrow(() -> new IllegalArgumentException("해당 채팅방에 속한 사용자가 아닙니다."));

        /**개인 채팅방에서 상대방 탈퇴 시 메시지 송신 차단**/
        if (chattingRoom.getRoomType() == RoomType.P) {
            List<ChattingParticipant> participants = chattingParticipantRepository.findByChattingRoomPk(chattingRoom);

            boolean hasOtherUser = participants.stream()
                    .anyMatch(participant -> !participant.getUserPk().getUserPk().equals(user.getUserPk()));

            if (!hasOtherUser) {
                throw new IllegalStateException("상대방이 탈퇴하여 메시지를 보낼 수 없습니다.");
            }
        }


        //Redis 발행용 데이터 세팅 (시간 포맷 적용)
        chattingMessageDto.setCreatedAt(now);
        chattingMessageDto.setNickName(user.getNickname());
        chattingMessageDto.setChattingMessageType(ChattingMessageType.CHAT);

        // 5. MySQL 저장
        chattingMessageRepository.save(
                chattingMessageDto.toEntity(chattingRoom, user, now)
        );

        // 6. MongoDB 저장
        chattingMessageMongoRepository.save(
                chattingMessageDto.toDocument(user.getNickname(), now)
        );



        // 8. Redis 발행
        redisPublisher.publish("chat", chattingMessageDto);
    }


    @Override
    public User addChattingParticipant(ChattingRoom chattingRoom, Long userPk, LocalDateTime now){

        // User 엔티티 조회
        User user = userRepository.findById(userPk)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        // 중복 참여자 방지
        boolean alreadyInRoom = chattingParticipantRepository
                .findByChattingRoomPkAndUserPk(chattingRoom, user)
                .isPresent();

        if (alreadyInRoom) return user;

        // 참여자 저장
        chattingParticipantRepository.save(ChattingParticipant.create(chattingRoom, user, now));
        return user;
    }

    // 채팅방 입장 메세지 출력 및 저장
    public void noticeJoinChattingRoom(ChattingRoom chattingRoom, User userPk, LocalDateTime now){

        // 실시간 입장 알림 DTO 생성
        ChattingMessageDto joinMessage = ChattingMessageDto.builder()
                .chattingRoomPk(chattingRoom.getChattingRoomPk())
                .userPk(userPk.getUserPk())
                .nickName(userPk.getNickname())
                .message(userPk.getNickname() + " 님이 입장했습니다.")
                .createdAt(now)
                .chattingMessageType(ChattingMessageType.JOIN) // 메시지 타입 설정
                .build();

        // DB 저장 (MySQL + MongoDB)
        chattingMessageRepository.save(joinMessage.toEntity(chattingRoom, userPk, now));
        chattingMessageMongoRepository.save(joinMessage.toDocument(userPk.getNickname(), now));

        //Redis 발행 (채널명은 고정)
        redisPublisher.publish("chat", joinMessage);

        // 입장 후 실시간 참여자 리스트 전송
        List<ChattingUserDto> participants = getChattingRoomParticipants(chattingRoom.getChattingRoomPk()); //
        redisPublisher.publishParticipantList(chattingRoom.getChattingRoomPk(), participants); //
    }


    /** 프로젝트에 멤버 추가 시 새로운 멤버와 기존 멤버간 개인 채팅방 모두 생성 **/
    @Override
    @Transactional
    public void createAllPrivateRoomsForNewMember(Project project, User newMember, LocalDateTime now) {
        ChattingRoom teamChattingRoom = getChattingRoom(project.getProjectPk(), RoomType.T);
        List<ChattingParticipant> currentParticipants =
                chattingParticipantRepository.findByChattingRoomPk(teamChattingRoom);

        for (ChattingParticipant participant : currentParticipants) {
            User existingUser = participant.getUserPk();

            if (existingUser.getUserPk().equals(newMember.getUserPk())) continue;

            Optional<ChattingRoom> existingRoomOpt =
                    chattingRoomRepository.findByProjectPkAndRoomTypeAndParticipants(
                            project.getProjectPk(),
                            RoomType.P,
                            existingUser.getUserPk(),
                            newMember.getUserPk()
                    );

            if (existingRoomOpt.isPresent()) {
                ChattingRoom existingRoom = existingRoomOpt.get();
                addChattingParticipant(existingRoom, existingUser.getUserPk(), now); // 내부에서 중복 무시
                addChattingParticipant(existingRoom, newMember.getUserPk(), now);
                continue;
            }

            ChattingRoom privateRoom = createPrivateChattingRoom(
                    project.getProjectPk(), RoomType.P,
                    existingUser.getUserPk(), newMember.getUserPk(), now
            );
            addChattingParticipant(privateRoom, existingUser.getUserPk(), now);
            addChattingParticipant(privateRoom, newMember.getUserPk(), now);
        }
    }


    /** 멤버를 삭제해야할 채팅방 조회 및 삭제 메서드 호출 **/
    // CHANGE: "프로젝트 내 모든 채팅방(T/P)에서 해당 유저 제거" 메서드 추가
    @Transactional
    public void performRemoveUserFromAllChattingRooms(Long projectPk, Long userPk, LocalDateTime now) {

        User user = userRepository.findById(userPk)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        // 이 프로젝트에서 이 유저가 연결된 모든 참여 레코드(팀/개인 전부)
        List<ChattingParticipant> links =
                chattingParticipantRepository
                        .findByUserPk_UserPkAndChattingRoomPk_ProjectPk_ProjectPk(userPk, projectPk);

        // 방 기준 중복 제거 후, 기존 removeChattingParticipant 재사용
        links.stream()
                .map(ChattingParticipant::getChattingRoomPk)
                .distinct()
                .forEach(room -> removeChattingParticipant(room, user, now));
    }



    /** 채팅방 삭제 및 퇴장 메세지 출력 **/
    @Override
    public void removeChattingParticipant(ChattingRoom chattingRoom, User user, LocalDateTime now) {
        // 1. DB 에서 참여자 제거
        chattingParticipantRepository.findByChattingRoomPkAndUserPk(chattingRoom, user)
                .ifPresent(chattingParticipantRepository::delete);

        // 3. 퇴장 메시지 DTO
        ChattingMessageDto leaveMessage = ChattingMessageDto.builder()
                .chattingRoomPk(chattingRoom.getChattingRoomPk())
                .userPk(user.getUserPk())
                .nickName(user.getNickname())
                .message(user.getNickname() + " 님이 퇴장했습니다.")
                .createdAt(now)
                .chattingMessageType(ChattingMessageType.LEAVE) // 퇴장 메시지 타입
                .build();

        // 4. DB 저장
        chattingMessageRepository.save(leaveMessage.toEntity(chattingRoom, user, now));
        chattingMessageMongoRepository.save(leaveMessage.toDocument(user.getNickname(), now));

        // 5. Redis 발행
        redisPublisher.publish("chat", leaveMessage);

        // 퇴장 후 실시간 참여자 리스트 전송
        List<ChattingUserDto> participants = getChattingRoomParticipants(chattingRoom.getChattingRoomPk()); //
        redisPublisher.publishParticipantList(chattingRoom.getChattingRoomPk(), participants); //

    }

    //채팅방 참여자 리스트 불러오기
    @Override
    @Transactional(readOnly = true) //조회 중 세션 유지
    public List<ChattingUserDto> getChattingRoomParticipants(Long chattingRoomPk) {
        ChattingRoom chattingRoom = chattingRoomRepository.findById(chattingRoomPk)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));

        RoomType roomType = chattingRoom.getRoomType(); // roomType 가져오기
        Project projectPk = chattingRoom.getProjectPk(); // 수정: 리더 확인을 위한 project 추출


        // 변경: fetch join 으로 User 까지 한 번에 로딩
        List<ChattingParticipant> participants =
                chattingParticipantRepository.findWithUserByChattingRoomPk(chattingRoom);

        return participants.stream()
                .map(chattingParticipant -> {
                    boolean isLeader = false;

                    // roomType 이 T일 때만 리더 여부 체크
                    if (RoomType.T.equals(roomType)) {
                        isLeader = projectMemberRepository
                                .findByProjectPkAndUserPk(projectPk, chattingParticipant.getUserPk())
                                .map(pm -> pm.getIsLeader() == IsLeader.LEADER) // ← enum 은 == 로 비교
                                .orElse(false);
                    }

                    boolean online = chattingPresenceService
                            .isOnline(chattingRoomPk, chattingParticipant.getUserPk().getUserPk()); // 온라인 상태 추가

                    return ChattingUserDto.builder() // 빌더 사용
                            .userPk(chattingParticipant.getUserPk().getUserPk())
                            .nickName(chattingParticipant.getUserPk().getNickname())
                            .leader(isLeader)
                            .online(online) // 온라인 상태 추가
                            .build();
                })
                .toList();
    }


    //개인 채팅방에서 상대방 존재 여부 확인
    @Override
    public boolean hasOtherUser(ChattingRoom chattingRoom, Long myUserPk) {
        return chattingParticipantRepository.findByChattingRoomPk(chattingRoom).stream()
                .anyMatch(p -> !p.getUserPk().getUserPk().equals(myUserPk));
    }


    //개인 채팅방 생성 시 상대가 현재 프로젝트 멤버인지 체크
    @Override
    public boolean isProjectMember(Long projectPk, Long userPk) {
        Project p = projectRepository.findById(projectPk)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트가 존재하지 않습니다."));
        return projectMemberRepository.findByProjectPkAndUserPk(p,
                        userRepository.findById(userPk).orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다.")))
                .isPresent();
    }

    @Override
    public List<ChattingRoomResponse> getMyPrivateChattingRooms(Long projectPk, Long userPk) {
        // 내가 참여자로 들어가 있는 개인(P) 채팅방들
        List<ChattingRoom> privateChattingRooms = chattingRoomRepository
                .findMyPrivateRoomsInProject(projectPk, RoomType.P, userPk);

        return privateChattingRooms.stream()
                .map(chattingRoom -> {
                    List<ChattingUserDto> participants =
                            getChattingRoomParticipants(chattingRoom.getChattingRoomPk());
                    boolean canSend = hasOtherUser(chattingRoom, userPk); // 상대 존재 여부

                    return ChattingRoomResponse.builder()
                            .chattingRoomPk(chattingRoom.getChattingRoomPk())
                            .roomType(RoomType.P)
                            .chattingRoomParticipants(participants)
                            .canSendMessage(canSend)
                            .build();
                })
                .toList();
    }

    // 프로젝트의 모든 채팅방을 소프트 삭제 처리하는 메서드
    @Transactional
    public void softDeleteAllChattingRoomsByProject(Long projectPk, Long userPk) { // [추가]
        Project project = projectRepository.findById(projectPk)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트가 존재하지 않습니다."));

        // 유저 조회
        User user = userRepository.findById(userPk)
                .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));

        // 프로젝트 리더 검증
        projectMemberRepository.findByProjectPkAndUserPk(project, user)
                .filter(projectMember -> projectMember.getIsLeader() == IsLeader.LEADER)
                .orElseThrow(() -> new IllegalStateException("프로젝트 리더만 삭제할 수 있습니다."));

        // 해당 프로젝트의 모든 채팅방 조회
        List<ChattingRoom> chattingRooms = chattingRoomRepository.findByProjectPk(project);

        for (ChattingRoom chattingRoom : chattingRooms) {
            chattingRoom.markAsDeleted(); // [추가] 엔티티 메서드 활용
            chattingRoomRepository.save(chattingRoom);
        }
    }



    /** 헬버 메서드 **/
    private void validateProjectMember(Project project, Long userPk) {
        User user = userRepository.findById(userPk)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        boolean isMember = projectMemberRepository
                .findByProjectPkAndUserPk(project, user)
                .isPresent();

        if (!isMember) {
            throw new IllegalArgumentException("프로젝트 멤버가 아닌 유저입니다: userPk = " + userPk);
        }
    }

}