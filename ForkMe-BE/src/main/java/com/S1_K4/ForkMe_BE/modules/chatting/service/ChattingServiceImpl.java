package com.S1_K4.ForkMe_BE.modules.chatting.service;

import com.S1_K4.ForkMe_BE.global.common.redis.RedisPublisher;
import com.S1_K4.ForkMe_BE.modules.chatting.chatting_enum.ChattingMessageType;
import com.S1_K4.ForkMe_BE.modules.chatting.chatting_enum.RoomType;
import com.S1_K4.ForkMe_BE.modules.chatting.dto.ChattingMessageDto;
import com.S1_K4.ForkMe_BE.modules.chatting.dto.ChattingUserDto;
import com.S1_K4.ForkMe_BE.modules.chatting.entity.ChattingParticipant;
import com.S1_K4.ForkMe_BE.modules.chatting.entity.ChattingRoom;
import com.S1_K4.ForkMe_BE.modules.chatting.repository.ChattingMessageMongoRepository;
import com.S1_K4.ForkMe_BE.modules.chatting.repository.ChattingMessageRepository;
import com.S1_K4.ForkMe_BE.modules.chatting.repository.ChattingParticipantRepository;
import com.S1_K4.ForkMe_BE.modules.chatting.repository.ChattingRoomRepository;
import com.S1_K4.ForkMe_BE.modules.project.entity.Project;
import com.S1_K4.ForkMe_BE.modules.user.entity.User;
import com.S1_K4.ForkMe_BE.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;

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
//    private final ProjectMemberRepository projectMemberRepository;


    @Override
    public ChattingRoom createChattingRoom(Project project, RoomType roomType, LocalDateTime now) {
        // 채팅방 생성
        ChattingRoom chattingRoom = ChattingRoom.create(project, roomType, now);
        chattingRoomRepository.save(chattingRoom);

        return chattingRoom;
    }

    @Override
    public ChattingRoom getChattingRoom(Project projectPk){
        return chattingRoomRepository.findByProjectPk(projectPk)
                .orElseThrow(() -> new IllegalStateException("채팅방이 없습니다."));
    }


    @Override
    public void sendMessage(ChattingMessageDto chattingMessageDto) {

        //서버용 시간은 UTC 시간으로 포맷 맞추기
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        // 프론트에 보낼 시간은 KST 로 변환해서 DTO 에 세팅
        LocalDateTime nowKST = now.atZone(ZoneOffset.UTC)
                .withZoneSameInstant(ZoneId.of("Asia/Seoul"))
                .toLocalDateTime();

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

        //Redis 발행용 데이터 세팅 (시간 포맷 적용)
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        chattingMessageDto.setCreatedAt(nowKST);
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
    public void addChattingParticipant(ChattingRoom chattingRoom, User userPk, LocalDateTime now){
        // 중복 참여자 방지
        boolean alreadyInRoom = chattingParticipantRepository
                .findByChattingRoomPkAndUserPk(chattingRoom, userPk)
                .isPresent();

        if (alreadyInRoom) return;

        // 참여자 저장
        chattingParticipantRepository.save(ChattingParticipant.create(chattingRoom, userPk, now));



        // 프론트에 보낼 시간은 KST 로 변환해서 DTO 에 세팅
        LocalDateTime nowKST = now.atZone(ZoneOffset.UTC)
                .withZoneSameInstant(ZoneId.of("Asia/Seoul"))
                .toLocalDateTime();

        // 실시간 입장 알림 DTO 생성
        ChattingMessageDto joinMessage = ChattingMessageDto.builder()
                .chattingRoomPk(chattingRoom.getChattingRoomPk())
                .userPk(userPk.getUserPk())
                .nickName(userPk.getNickname())
                .message(userPk.getNickname() + " 님이 참여했습니다.")
                .createdAt(nowKST)
                .chattingMessageType(ChattingMessageType.JOIN) // ✅ 메시지 타입 설정
                .build();

        // DB 저장 (MySQL + MongoDB)
        chattingMessageRepository.save(joinMessage.toEntity(chattingRoom, userPk, now));
        chattingMessageMongoRepository.save(joinMessage.toDocument(userPk.getNickname(), now));

        //Redis 발행 (채널명은 고정)
        redisPublisher.publish("chat", joinMessage);
    }

    @Override
    public void removeChattingParticipant(ChattingRoom chattingRoom, User user, LocalDateTime now) {
        // 1. DB 에서 참여자 제거
        chattingParticipantRepository.findByChattingRoomPkAndUserPk(chattingRoom, user)
                .ifPresent(chattingParticipantRepository::delete);

        // 2. 프론트에 보낼 시간 (KST)
        LocalDateTime nowKST = now.atZone(ZoneOffset.UTC)
                .withZoneSameInstant(ZoneId.of("Asia/Seoul"))
                .toLocalDateTime();

        // 3. 퇴장 메시지 DTO
        ChattingMessageDto leaveMessage = ChattingMessageDto.builder()
                .chattingRoomPk(chattingRoom.getChattingRoomPk())
                .userPk(user.getUserPk())
                .nickName(user.getNickname())
                .message(user.getNickname() + " 님이 퇴장했습니다.")
                .createdAt(nowKST)
                .chattingMessageType(ChattingMessageType.LEAVE) // 퇴장 메시지 타입
                .build();

        // 4. DB 저장
        chattingMessageRepository.save(leaveMessage.toEntity(chattingRoom, user, now));
        chattingMessageMongoRepository.save(leaveMessage.toDocument(user.getNickname(), now));

        // 5. Redis 발행
        redisPublisher.publish("chat", leaveMessage);
    }

    //채팅방 참여자 리스트 불러오기
    @Override
    public List<ChattingUserDto> getTeamChattingRoomParticipants(Long chattingRoomPk) {
        ChattingRoom chattingRoom = chattingRoomRepository.findById(chattingRoomPk)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));

        Project projectPk = chattingRoom.getProjectPk(); // ✅ 수정: 리더 확인을 위한 project 추출

        return chattingParticipantRepository.findByChattingRoomPk(chattingRoom)
                .stream()
                .map(cp -> {
//                    boolean isLeader = projectMemberRepository
//                            .findByProjectPkAndUserPk(projectPk, cp.getUserPk())
//                            .map(projectMember -> "leader".equals(projectMember.getIsLeader()))
//                            .orElse(false);

                    return ChattingUserDto.builder() // ✅ 빌더 사용
                            .userPk(cp.getUserPk().getUserPk())
                            .nickName(cp.getUserPk().getNickname())
//                            .leader(isLeader)
                            .build();
                })
                .toList();
    }

}
