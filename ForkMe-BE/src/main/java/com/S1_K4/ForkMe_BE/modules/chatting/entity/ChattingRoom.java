package com.S1_K4.ForkMe_BE.modules.chatting.entity;

import com.S1_K4.ForkMe_BE.global.common.common_enum.Yn;
import com.S1_K4.ForkMe_BE.modules.chatting.chatting_enum.RoomType;
import com.S1_K4.ForkMe_BE.modules.project.entity.Project;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : 김남이
 * @packageName : com.S1_K4.ForkMe_BE.modules.chatting.entity
 * @fileName : ChattingRoom
 * @date : 2025-08-06
 * @description : 채팅방 엔티티 입니다.
 */
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "chatting_room")
public class ChattingRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chatting_room_pk")
    private Long chattingRoomPk;

    @Enumerated(EnumType.STRING)
    @Column(name = "room_type", length = 10)
    private RoomType roomType = RoomType.T;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "deleted_yn", nullable = false, length = 1)
    private Yn deletedYn = Yn.N;

    @ManyToOne(fetch = FetchType.LAZY) // ✅ 프로젝트와 연관관계
    @JoinColumn(name = "project_pk", nullable = false)
    private Project projectPk;

    @OneToMany(mappedBy = "chattingRoomPk", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChattingParticipant> chattingParticipants = new ArrayList<>();

    //정적 팩토리 메서드
    public static ChattingRoom create(Project project, RoomType roomType, LocalDateTime now){

        return ChattingRoom.builder()
                .projectPk(project)
                .roomType(roomType)
                .deletedYn(Yn.N)
                .createdAt(now)
                .build();
    }

    public void addParticipant(ChattingParticipant participant){
        chattingParticipants.add(participant);
        participant.setChattingRoomPk(this);
    }

    public void removeParticipant(ChattingParticipant participant){
        chattingParticipants.remove(participant);
        participant.setChattingRoomPk(null);
    }

}

