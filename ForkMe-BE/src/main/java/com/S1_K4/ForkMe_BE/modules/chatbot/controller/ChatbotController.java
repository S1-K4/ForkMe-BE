package com.S1_K4.ForkMe_BE.modules.chatbot.controller;

import com.S1_K4.ForkMe_BE.modules.chatbot.app.ProjectTopicBotService;
import com.S1_K4.ForkMe_BE.modules.chatbot.dto.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
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
                              @Header(name = "simpSessionId", required = false) String sessionId) {

        // simpSessionId 확보
        String sid = (sessionId != null) ? sessionId : headers.getSessionId();

        // Handshake Interceptor에서 넣은 userPk (없어도 동작)
        Long userPk = null;
        Object attr = headers.getSessionAttributes() != null ? headers.getSessionAttributes().get("userPk") : null;
        if (attr instanceof Long l) userPk = l;
        else if (attr instanceof Integer i) userPk = i.longValue();

        // 비즈니스 로직 호출
        ChatMessage[] outs = bot.handle(sid, userPk, incoming.getMessage());

        // 사용자 에코 + 봇 답장을 순차 브로드캐스트
        for (ChatMessage m : outs) {
            template.convertAndSend("/topic/gpt", m);
        }
    }
}