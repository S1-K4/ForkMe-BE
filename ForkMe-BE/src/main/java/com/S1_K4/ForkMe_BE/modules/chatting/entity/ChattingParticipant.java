package com.S1_K4.ForkMe_BE.modules.chatting.entity;

import com.S1_K4.ForkMe_BE.global.common.entity.BaseTime;
import com.S1_K4.ForkMe_BE.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author : 김남이
 * @packageName : com.S1_K4.ForkMe_BE.modules.chatting.entity
 * @fileName : ChattingParticipant
 * @date : 2025-08-06
 * @description : 채팅 참여자 엔티티입니다.
 */
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "chatting_participant")
public class ChattingParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chatting_participant_pk")
    private Long chattingParticipantPk;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatting_room_pk", nullable = false)
    private ChattingRoom chattingRoomPk;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_pk", nullable = false)
    private User userPk;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

//    @Enumerated(EnumType.STRING)
//    @Column(name = "deleted_yn", nullable = false, length = 1)
//    private Yn deletedYn = Yn.N;

    public static ChattingParticipant create(ChattingRoom chattingRoomPk, User userPk, LocalDateTime now){
        return ChattingParticipant.builder()
                .chattingRoomPk(chattingRoomPk)
                .userPk(userPk)
//                .deletedYn(Yn.N)
                .createdAt(now)
                .build();
    }

    public void setChattingRoomPk(ChattingRoom chattingRoomPk){
        this.chattingRoomPk = chattingRoomPk;
    }
}
