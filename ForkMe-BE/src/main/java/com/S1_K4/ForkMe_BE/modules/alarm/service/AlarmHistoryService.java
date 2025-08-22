package com.S1_K4.ForkMe_BE.modules.alarm.service;

import com.S1_K4.ForkMe_BE.modules.alarm.dto.AlarmListResponse;

/**
 * @author : 김남이
 * @packageName : com.S1_K4.ForkMe_BE.modules.alarm.service
 * @fileName : AlarmHistoryService
 * @date : 2025-08-20
 * @description : 이전 알람 내역 비즈니스 로직 선언
 */
public interface AlarmHistoryService {

    AlarmListResponse getUserAlarms(Long userPk, int page, int size) ;
}
