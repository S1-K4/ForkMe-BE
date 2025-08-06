package com.S1_K4.ForkMe_BE.global.common.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

/**
 * @author : 김관중
 * @packageName : com.S1_K4.ForkMe_BE.global.common.redis
 * @fileName : WebSocketEventListener
 * @date : 2025-08-04
 * @description : websocket과 redis를 연결한 이벤트 핸들러
 */
@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final RedisService redisService;
    private final ViewerBroadcaster viewerBroadcaster;
    private final RedisViewerSessionService sessionService;

    //연결시
    @EventListener
    public void handlerWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        String sessionId = accessor.getSessionId();
        String userPkStr = accessor.getFirstNativeHeader("userPk");
        String projectPkStr = accessor.getFirstNativeHeader("projectPk");

        if(userPkStr == null || projectPkStr == null) return;

        Long userPk = Long.parseLong(userPkStr);
        Long projectPk = Long.parseLong(projectPkStr);

        sessionService.saveSessionInfo(sessionId, userPk, projectPk);
        redisService.enterPost(projectPk,userPk);

        viewerBroadcaster.broadcastViewerCount(projectPk);
    }

    //연결 해제시
    @EventListener
    public void handlerWebSocketDisconnectListener(SessionDisconnectEvent event) {
        String sessionId = StompHeaderAccessor.wrap(event.getMessage()).getSessionId();

        RedisViewerSessionService.ViewerInfo info = sessionService.getSessionInfo(sessionId);
        if(info != null) {
            redisService.leavePost(info.projectPk(), info.userPk());
            viewerBroadcaster.broadcastViewerCount(info.projectPk());
            sessionService.deleteSessionInfo(sessionId);
        }
    }
}
