package com.S1_K4.ForkMe_BE.modules.alarm.service;

import com.S1_K4.ForkMe_BE.modules.apply.entity.Apply;
import com.S1_K4.ForkMe_BE.modules.chatting.dto.ChattingUserDto;
import com.S1_K4.ForkMe_BE.modules.chatting.entity.ChattingRoom;
import com.S1_K4.ForkMe_BE.modules.project.entity.Project;
import com.S1_K4.ForkMe_BE.modules.user.entity.User;
import org.w3c.dom.stylesheets.LinkStyle;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author : 김남이
 * @packageName : com.S1_K4.ForkMe_BE.modules.alarm.service
 * @fileName : AlarmService
 * @date : 2025-08-19
 * @description : 알람 기능 비즈니스 로직 선언
 */
public interface                                                                                                                                                                                                                                                                                                                              AlarmService {

    //새로운 신청서 접수 시 프로젝트 리더에게 알림 발송
    void alarmApplyToLeader(User applicant, Project project, Apply apply);

    //신청서 수락 시 프로젝트 지원자에게 알림 발송
    void alarmApplyToApplicant(User applicant, Project project, Apply apply, LocalDateTime now);

    //프로젝트 내의 채팅방(팀/개인)에서 새로운 채팅 발생 시 알림 발송
    void alarmChattingMessageToMember(
            Long chattingRoomPk,
            User teamMember,
            LocalDateTime now,
            List<ChattingUserDto> participantsDetails
    );

    //알람 단건 삭제
    String removeAlarm(String alarmPk);

    // 알림 읽음 처리
    void readAllAlarms(Long userPk);

    //알람 전체 삭제
    void removeAllAlarms(Long userPk);

}
