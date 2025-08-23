package com.S1_K4.ForkMe_BE.modules.chatbot.controller;

import com.S1_K4.ForkMe_BE.modules.chatbot.service.ProjectTopicBotService;
import com.S1_K4.ForkMe_BE.modules.chatbot.dto.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.chatbot.controller
 * @fileName : ChatbotController
 * @date : 2025-08-20
 * @description : 챗봇 컨트롤러
 */
@Controller
@RequiredArgsConstructor
public class ChatbotController {

    private final ProjectTopicBotService bot;
    private final SimpMessagingTemplate template;

    @MessageMapping("/gpt")
    public void onUserMessage(ChatMessage incoming,
                              SimpMessageHeaderAccessor headers,
                              @Header(name = "simpSessionId", required = false) String sessionId,
                              Principal principal) {

        // 세션/사용자 키 결정
        final String sid = (sessionId != null) ? sessionId : headers.getSessionId();
        final String userKey = (principal != null) ? principal.getName() : sid; // 익명도 OK

        // 비즈니스 로직 호출 (서비스는 비동기 응답도 convertAndSendToUser로 보냄)
        ChatMessage[] outs = bot.handle(sid, null, incoming.getMessage(), userKey);

        // 즉시 응답도 사용자별 큐로 전송 (여기서 브로드캐스트 금지!)
        for (ChatMessage m : outs) {
            template.convertAndSendToUser(userKey, "/queue/gpt", m);
        }
    }
}