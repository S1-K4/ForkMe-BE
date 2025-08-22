package com.S1_K4.ForkMe_BE.modules.chatbot.domain;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.chatbot
 * @fileName : BotState
 * @date : 2025-08-20
 * @description : 챗봇 상태 도메인(챗봇이 지금 대화에서 어디까지 왔는지 단계를 열거형으로 관리)
 */
public enum BotState {
    START,              // 초기 상태 (인사/시작 안내)
    AWAIT_YES,          // "네" 대기
    ASK_STACK,          // 기술스택 질문
    ASK_DURATION,       // 예상 기간 질문
    ASK_MEMBERS,        // 예상 인원 질문
    READY_TO_SUGGEST,   // 3개 추천 생성 가능
    ASK_MORE            // "더 추천해드릴까요?" 단계

}
