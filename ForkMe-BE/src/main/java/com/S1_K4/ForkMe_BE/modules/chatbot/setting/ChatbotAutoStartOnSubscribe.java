package com.S1_K4.ForkMe_BE.modules.chatbot.setting;

import com.S1_K4.ForkMe_BE.modules.chatbot.domain.BotSession;
import com.S1_K4.ForkMe_BE.modules.chatbot.domain.BotState;
import com.S1_K4.ForkMe_BE.modules.chatbot.dto.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.chatbot.setting
 * @fileName : ChatbotAutoStartOnSubscribe
 * @date : 2025-08-23
 * @description :클라이언트가 /user/queue/gpt 구독을 시작하면 챗봇이 자동으로 인사 메시지를 보내주는 리스너
 */
@Component
@RequiredArgsConstructor
public class ChatbotAutoStartOnSubscribe {

    private final BotSessionStore store;
    private final SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void onSubscribe(SessionSubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String dest = accessor.getDestination();
        if (dest == null) return;

        // 챗봇 개인 큐 구독일 때만 반응
        if (!dest.endsWith("/queue/gpt")) return;

        String sessionId = accessor.getSessionId();
        String userKey = (accessor.getUser() != null) ? accessor.getUser().getName() : sessionId;

        // 세션 상태 확인
        BotSession s = store.getOrCreate(sessionId, null);
        if (s.getState() == BotState.START) {
            // START 상태일 때만 인사 → 중복 인사 방지
            s.setState(BotState.AWAIT_YES);
            store.save(s);

            ChatMessage hi = new ChatMessage("GPT-봇",
                    "안녕하세요! 저는 **프로젝트 주제추천 챗봇**이에요.\n" +
                            "주제추천을 시작할까요? (답변: 네)");
            messagingTemplate.convertAndSendToUser(userKey, "/queue/gpt", hi);
        }
    }
}