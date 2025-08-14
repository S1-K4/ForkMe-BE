package com.S1_K4.ForkMe_BE.modules.chatting.controller;

import com.S1_K4.ForkMe_BE.modules.auth.dto.CustomUserDetails;
import com.S1_K4.ForkMe_BE.modules.chatting.chatting_enum.RoomType;
import com.S1_K4.ForkMe_BE.modules.chatting.dto.ChattingMessageDto;
import com.S1_K4.ForkMe_BE.modules.chatting.dto.ChattingUserDto;
import com.S1_K4.ForkMe_BE.modules.chatting.dto.ChattingRoomResponse;
import com.S1_K4.ForkMe_BE.modules.chatting.entity.ChattingRoom;
import com.S1_K4.ForkMe_BE.modules.chatting.service.ChattingService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

/**
 * @author : 김남이
 * @packageName : com.S1_K4.ForkMe_BE.modules.chatting.controller
 * @fileName : ChattingController
 * @date : 2025-08-07
 * @description : 실시간 채팅 담당 컨트롤러 입니다.
 */
@RestController
@RequestMapping("/api/chatting-room")
@RequiredArgsConstructor
public class ChattingController {

    private final ChattingService chattingService;

    /**
     * STOMP로 "/app/chat/message" 경로로 들어오는 메시지 처리
     */
    @MessageMapping("/chat/message")
    public void sendMessage(ChattingMessageDto chattingMessageDto) {
        chattingService.sendMessage(chattingMessageDto);
    }

    /**
     * 채팅방 참여자 리스트 조회
     */
    //채팅방 참여자 조회(참여자 조회에 권한 체크 필요하면 나중에 추가)
    @GetMapping("/{chattingRoomPk}/participants")
    public List<ChattingUserDto> getChattingRoomParticipants(
            @PathVariable("chattingRoomPk") Long chattingRoomPk) {
        return chattingService.getChattingRoomParticipants(chattingRoomPk);
    }


    @GetMapping("/create")
    public ChattingRoomResponse createPrivateChattingRoom(
            @RequestParam("projectPk") Long projectPk,
            @RequestParam("roomType") RoomType roomType,
//            @RequestParam(value = "fromUserPk", required = false) Long fromUserPk,
            @RequestParam(value = "toUserPk", required = false) Long toUserPk,
            @AuthenticationPrincipal CustomUserDetails userDetails

    ){
        //생성 시간 UTC
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        Long fromUserPk = userDetails.getUserPk();

        ChattingRoom chattingRoom;
        List<ChattingUserDto> participants;
        boolean canSendMessage = true; // 기본값 true (팀 채팅은 항상 true)


        if (roomType == RoomType.T) {
            chattingRoom = chattingService.getChattingRoom(projectPk, roomType);
            participants = chattingService.getChattingRoomParticipants(chattingRoom.getChattingRoomPk());

        } else if (roomType == RoomType.P) {
            if (fromUserPk == null || toUserPk == null) {
                throw new IllegalArgumentException("개인 채팅방 생성 시 fromUserPk, toUserPk는 필수입니다.");
            }

            // 방 생성 or 조회
            chattingRoom = chattingService.createPrivateChattingRoom(projectPk, roomType, fromUserPk, toUserPk, now);

            // 필요시 참여자 등록 (중복이면 내부에서 무시됨)
            // CHANGED: 현재 멤버만 참여자로 추가 (탈퇴자는 재등록 금지)
            if (chattingService.isProjectMember(projectPk, fromUserPk)) {
                chattingService.addChattingParticipant(chattingRoom, fromUserPk, now);
            }
            if (chattingService.isProjectMember(projectPk, toUserPk)) {
                chattingService.addChattingParticipant(chattingRoom, toUserPk, now);
            }


            // 참여자 조회
            participants = chattingService.getChattingRoomParticipants(chattingRoom.getChattingRoomPk());

            // 상대방 존재 여부
            canSendMessage = chattingService.hasOtherUser(chattingRoom, fromUserPk);

        } else {
            throw new IllegalArgumentException("유효하지 않은 채팅방 타입입니다.");
        }

        return ChattingRoomResponse.builder()
                .chattingRoomPk(chattingRoom.getChattingRoomPk())
                .roomType(roomType)
                .chattingRoomParticipants(participants)
                .canSendMessage(canSendMessage)
                .build();
    }


    //현재 프로젝트에 귀속된 개인 채팅방 리스트 보여주기
    @GetMapping("/private")
    public List<ChattingRoomResponse> getMyPrivateChattingRooms(
            @RequestParam("projectPk") Long projectPk,
            @RequestParam("userPk") Long userPk
    ) {
        return chattingService.getMyPrivateChattingRooms(projectPk, userPk);
    }


}