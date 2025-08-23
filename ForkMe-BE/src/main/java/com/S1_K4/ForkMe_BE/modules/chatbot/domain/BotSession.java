package com.S1_K4.ForkMe_BE.modules.chatbot.domain;

import lombok.*;

import java.time.Instant;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.chatbot
 * @fileName : BotSession
 * @date : 2025-08-20
 * @description : 챗봇 세션 도메인 -> 사용자별 대화의 진행상황 + 입력값(스택/기간/인원)을 저장하는 세션 스냅샷
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BotSession {
    private String sessionId;     // STOMP simpSessionId
    private Long userPk;          // Handshake 속성에서 읽은 userPk
    private BotState state;        //현재 상태
    private String techStack;     // 예: "Java, Spring, Redis"
    private String duration;      // 예: "2개월"
    private String members;       // 예: "3명"
    private int suggestionRounds; // 몇 라운드 추천했는지 (중복 회피용)
    private Instant updatedAt;
}