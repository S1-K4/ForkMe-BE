package com.S1_K4.ForkMe_BE.modules.chatting.service;

import com.S1_K4.ForkMe_BE.modules.chatting.dto.ChattingMessageDto;
import com.S1_K4.ForkMe_BE.modules.chatting.entity.ChattingRoom;
import com.S1_K4.ForkMe_BE.modules.chatting.repository.ChattingMessageMongoRepository;
import com.S1_K4.ForkMe_BE.modules.chatting.repository.ChattingParticipantRepository;
import com.S1_K4.ForkMe_BE.modules.chatting.repository.ChattingRoomRepository;
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
 * @fileName : ChattingHistoryServiceImpl
 * @date : 2025-08-07
 * @description : 이전 채팅 내역 관련 서비스 구현
 */
@Service
@RequiredArgsConstructor
public class ChattingHistoryServiceImpl implements ChattingHistoryService{

    private final ChattingMessageMongoRepository chattingMessageMongoRepository;
    private final ChattingParticipantRepository chattingParticipantRepository;
    private final ChattingRoomRepository chattingRoomRepository;
    private final UserRepository userRepository;

    public List<ChattingMessageDto> getChattingHistory(Long chattingRoomPk, Long userPk) {
        ChattingRoom room = chattingRoomRepository.findById(chattingRoomPk)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));

        User user = userRepository.findById(userPk)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        // 유저의 채팅방 참여 시간 확인
        LocalDateTime joinTime = chattingParticipantRepository
                .findByChattingRoomPkAndUserPk(room, user)
                .orElseThrow(() -> new IllegalArgumentException("채팅방에 참여하지 않은 유저입니다."))
                .getCreatedAt();

        // 참여 시간 이후의 메시지만 가져오기
        return chattingMessageMongoRepository
                .findByChattingRoomPkAndCreatedAtAfterOrderByCreatedAtAsc(chattingRoomPk, joinTime)
                .stream()
                .map(document -> {
                    // KST 로 시간 변환
                    LocalDateTime kstTime = document.getCreatedAt()
                            .atZone(ZoneOffset.UTC)
                            .withZoneSameInstant(ZoneId.of("Asia/Seoul"))
                            .toLocalDateTime();

                    return ChattingMessageDto.builder()
                            .chattingRoomPk(document.getChattingRoomPk())
                            .userPk(document.getUserPk())
                            .nickName(document.getNickName())
                            .message(document.getMessage())
                            .chattingMessageType(document.getChattingMessageType())
                            .createdAt(kstTime)  //변환된 시간 사용
                            .build();
                })

                // 이전 채팅(입/퇴장 포함) 시간 순 정렬 한 번 더 보장
                .sorted((a, b) -> a.getCreatedAt().compareTo(b.getCreatedAt()))
                .toList();
    }
}

