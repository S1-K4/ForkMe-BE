package com.S1_K4.ForkMe_BE.modules.chatbot.setting;

import com.S1_K4.ForkMe_BE.modules.chatbot.service.ChatSessionSummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.chatbot.setting
 * @fileName : ChatbotSessionEventListener
 * @date : 2025-08-22
 * @description : 챗봇 -> 사용자가 탭을 닫거나 연결이 끊길때도 종료시간 기록하는 리스너
 */
@Component
@RequiredArgsConstructor
public class ChatbotSessionEventListener {

    private final ChatSessionSummaryService summaryService;

    /** 사용자가 탭을 닫거나 연결이 끊길 때도 종료 시간 기록 */
    @EventListener
    public void onDisconnect(SessionDisconnectEvent event) {
        String sessionId = StompHeaderAccessor.wrap(event.getMessage()).getSessionId();
        if (sessionId != null) {
            // 요약 문서가 있는 세션만 종료기록 (없으면 조용히 무시)
            summaryService.markEndedIfExists(sessionId);
        }
    }
}