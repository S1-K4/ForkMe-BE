package com.S1_K4.ForkMe_BE.modules.alarm.entity;

import com.S1_K4.ForkMe_BE.global.common.common_enum.Yn;
import com.S1_K4.ForkMe_BE.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author : 김남이
 * @packageName : com.S1_K4.ForkMe_BE.modules.alarm.entity
 * @fileName : Alarm
 * @date : 2025-08-19
 * @description : 알람 엔티티 입니다.
 */
//@Entity
//@Getter
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//@Table(name = "alarm")
//public class Alarm {
//
//    @Id
//    @GeneratedValue
//    @Column(name = "alarmPk", nullable = false)
//    private Long alarmPk;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @Column(name = "user_pk", nullable = false)
//    private User user;
//
//    @Column(name = "alarm_content", nullable = false)
//    private String alarmContent;
//
//    @Column(name = "alarm_type", nullable = false)
//    private String alarmType;
//
//    @Column(name = "reference_id", nullable = false)
//    private Long referenceId;
//
//    @Column(name = "read_yn")
//    private Yn readYn = Yn.N;   // 읽음 여부
//
//    @Column(name = "deleted_yn")
//    private Yn deletedYn = Yn.N; // 삭제 여부
//
//    @Column(name = "read_at", nullable = true)
//    private LocalDateTime readAt; // 읽은 시각(읽지 않았을 경우 null)
//
//    @Column(name = "created_at", nullable = false)
//    private LocalDateTime createdAt; //알림 생성 시간
//}
