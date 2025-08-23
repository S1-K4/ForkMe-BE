package com.S1_K4.ForkMe_BE.modules.alarm.service;

import com.S1_K4.ForkMe_BE.global.common.common_enum.Yn;
import com.S1_K4.ForkMe_BE.modules.alarm.dto.AlarmListResponse;
import com.S1_K4.ForkMe_BE.modules.alarm.dto.AlarmMessageRequest;
import com.S1_K4.ForkMe_BE.modules.alarm.dto.AlarmMessageResponse;
import com.S1_K4.ForkMe_BE.modules.alarm.repository.AlarmMessageMongoRepository;
import com.S1_K4.ForkMe_BE.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author : 김남이
 * @packageName : com.S1_K4.ForkMe_BE.modules.alarm.service
 * @fileName : AalrmHistoryServiceImpl
 * @date : 2025-08-20
 * @description : 이전 알람 내역 비즈니스 로직 구현
 */
@Service
@RequiredArgsConstructor
public class AlarmHistoryServiceImpl implements AlarmHistoryService{

    private final AlarmMessageMongoRepository alarmMessageMongoRepository;
    private final UserRepository userRepository;


    public AlarmListResponse getUserAlarms(Long userPk, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        // 알림 리스트 조회 (Document → DTO 변환)
        List<AlarmMessageResponse> alarmList = alarmMessageMongoRepository
                .findByUserPkAndDeletedYnOrderByCreatedAtDesc(userPk, Yn.N, pageable)
                .stream()
                .map(AlarmMessageResponse::fromDocument)
                .toList();

        // 미확인 + 삭제되지 않은 알림 개수
        int unreadCount = (int) alarmMessageMongoRepository
                .countByUserPkAndReadYnAndDeletedYn(userPk, Yn.N, Yn.N);

        // AlarmListResponse 로 감싸서 리턴
        return AlarmListResponse.builder()
                .alarms(alarmList)
                .unreadCount(unreadCount)
                .build();
    }
}
