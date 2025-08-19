package com.S1_K4.ForkMe_BE.modules.chatting.presence.event;

import com.S1_K4.ForkMe_BE.global.common.redis.RedisPublisher;
import com.S1_K4.ForkMe_BE.modules.chatting.presence.service.ChattingPresenceService;
import com.S1_K4.ForkMe_BE.modules.chatting.service.ChattingService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

/**
 * @author : 김남이
 * @packageName : com.S1_K4.ForkMe_BE.modules.chatting.presence.event
 * @fileName : ChatPresenceEventListener
 * @date : 2025-08-12
 * @description : 채팅 구독/해제 이벤트 리스너
 */
@Component
@RequiredArgsConstructor
public class ChattingPresenceEventListener {

    private final ChattingPresenceService presenceService;
    private final ChattingService chattingService;
    private final RedisPublisher redisPublisher;

    @EventListener
    public void onSubscribe(SessionSubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String dest = accessor.getDestination();           // e.g. /topic/chat/123
        String sessionId = accessor.getSessionId();
//        String userPkStr = accessor.getFirstNativeHeader("userPk"); //접속중 로그인 제거

        Object userPkObj = accessor.getSessionAttributes().get("userPk");

        if (dest == null || userPkObj == null) return; //접속중 로그인 userPkStr -> userPkObj
        if (!dest.startsWith("/topic/chat/")) return;

        Long roomPk = parseRoomPk(dest);
//        Long userPk = Long.valueOf(userPkStr);
        Long userPk = (Long) userPkObj; //접속중 로그인 추가

        // 동일 세션의 이전 구독 흔적 정리(레이스 방지)
        presenceService.onUnsubscribeOrDisconnect(sessionId);
        presenceService.onSubscribe(sessionId, roomPk, userPk);

        // 최신 참여자 리스트 브로드캐스트
        redisPublisher.publishParticipantList(
                roomPk,
                chattingService.getChattingRoomParticipants(roomPk)
        );
    }

    @EventListener
    public void onUnsubscribe(SessionUnsubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();
        Long roomPk = presenceService.onUnsubscribeOrDisconnect(sessionId);
        if (roomPk != null) {
            redisPublisher.publishParticipantList(
                    roomPk,
                    chattingService.getChattingRoomParticipants(roomPk)
            );
        }
    }

    @EventListener
    public void onDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();
        Long roomPk = presenceService.onUnsubscribeOrDisconnect(sessionId);
        if (roomPk != null) {
            redisPublisher.publishParticipantList(
                    roomPk,
                    chattingService.getChattingRoomParticipants(roomPk)
            );
        }
    }

    private Long parseRoomPk(String dest) {
        String s = dest.substring("/topic/chat/".length());
        int slash = s.indexOf('/');
        return Long.valueOf(slash >= 0 ? s.substring(0, slash) : s);
        // "/topic/chat/{chattingRoomPk}" 또는 "/topic/chat/{chattingRoomPk}/members" 모두 처리
    }
}