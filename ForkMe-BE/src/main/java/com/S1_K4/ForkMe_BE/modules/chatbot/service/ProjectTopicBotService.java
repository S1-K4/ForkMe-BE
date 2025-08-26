package com.S1_K4.ForkMe_BE.modules.chatbot.service;

import com.S1_K4.ForkMe_BE.modules.chatbot.domain.BotSession;
import com.S1_K4.ForkMe_BE.modules.chatbot.domain.BotState;
import com.S1_K4.ForkMe_BE.modules.chatbot.dto.ChatMessage;
import com.S1_K4.ForkMe_BE.modules.chatbot.service.GPTService;
import com.S1_K4.ForkMe_BE.modules.chatbot.setting.BotSessionStore;
import com.S1_K4.ForkMe_BE.modules.chatbot.setting.ProjectIdeaPromptBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.Executor;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.chatbot.app
 * @fileName : ProjectTopicBotService
 * @date : 2025-08-20
 * @description : projectTopicBotService
 * -> 세션 상태를 보고, 입력 검증하고, 필요 시 GTPService 호출 후 그 결과를 ChatMessage[]로 만들어 돌려줌.
 */
@Service
//@RequiredArgsConstructor
public class ProjectTopicBotService {

    private final BotSessionStore store;                        // 세션보관/TTL
    private final GPTService gpt;                               // LLM 호출기
    private final SimpMessagingTemplate messagingTemplate;      // STOMP 송신
    private final ChatSessionSummaryService summaryService;     //DB 저장용

    // 선택: 특정 실행기 사용하고 싶으면 주입
    @Qualifier("botExecutor")
    private final Executor botExecutor;                         // 오래 걸리는 작업용 스레드풀

    /** 생성자 직접 주입 방식으로 변경 **/
    public ProjectTopicBotService(
            BotSessionStore store,
            GPTService gpt,
            SimpMessagingTemplate messagingTemplate,
            ChatSessionSummaryService summaryService,
            @Qualifier("botExecutor") Executor botExecutor  // <-- 바로 여기에!
    ) {
        this.store = store;
        this.gpt = gpt;
        this.messagingTemplate = messagingTemplate;
        this.summaryService = summaryService;
        this.botExecutor = botExecutor;
    }
    /****/

    // 메시지에 GPT-봇을 찍어주는 헬퍼
    private ChatMessage bot(String text) {
        return new ChatMessage("GPT-봇", text);
    }

    /**
     * targetUserKey: 컨트롤러에서 전달한 Principal 이름(세션별 고유값)
     * - 즉시 응답은 컨트롤러가 convertAndSendToUser(...)로 보내야 함
     * - 비동기 응답은 여기서 convertAndSendToUser(...)로 바로 보냄
     */
    public ChatMessage[] handle(String sessionId, Long userPk, String incomingText, String targetUserKey) {
        BotSession s = store.getOrCreate(sessionId, userPk);
        String userMsg = incomingText == null ? "" : incomingText.trim();

        switch (s.getState()) {
            // 첫인사 + 다음상태로 이동
            case START -> {
                s.setState(BotState.AWAIT_YES);
                store.save(s);
                return new ChatMessage[]{ bot("안녕하세요! 저는 **프로젝트 주제추천 챗봇**이에요. 주제추천을 해드릴까요? (답변: 네)") };
            }

            // Yes/No만 허용하여 사용자 흐름 교육
            case AWAIT_YES -> {
                if (isYes(userMsg)) {
                    s.setState(BotState.ASK_STACK);
                    store.save(s);
                    return new ChatMessage[]{ bot("좋아요! 먼저 **프로젝트 기술스택**을 알려주세요. (예: Java, Spring, Redis)") };
                } else if (isNo(userMsg)) {
                    return new ChatMessage[]{ bot("준비가 되면 '네'라고 입력해 주세요!") };
                } else {
                    return new ChatMessage[]{ bot("시작하려면 **'네'**라고 입력해주세요. (예: 네)") };
                }
            }

            case ASK_STACK -> {
                if (!StringUtils.hasText(userMsg)) {
                    return new ChatMessage[]{ bot("기술스택을 입력해주세요. (예: Java, Spring, Redis)") };
                }
                s.setTechStack(userMsg);
                s.setState(BotState.ASK_DURATION);
                store.save(s);
                return new ChatMessage[]{ bot("다음으로 **예상 기간**을 알려주세요! (예: 1개월 / 6주 / 2개월)") };
            }

            case ASK_DURATION -> {
                if (!StringUtils.hasText(userMsg)) {
                    return new ChatMessage[]{ bot("예상 기간을 입력해주세요. (예: 2개월)") };
                }
                s.setDuration(userMsg);
                s.setState(BotState.ASK_MEMBERS);
                store.save(s);
                return new ChatMessage[]{ bot("마지막으로 **예상 인원**을 알려주세요! (예: 3명)") };
            }

            // 요약 + 로딩을 먼저 반환 -> 사용자 입장에서 챗봇이 제대로 작동하고 있구나 확인 가능
            case ASK_MEMBERS -> {
                if (!StringUtils.hasText(userMsg)) {
                    return new ChatMessage[]{ bot("인원 형식이 맞지 않아요. (예: 3명)") };
                }
                s.setMembers(userMsg);

                // 상태 전환은 ASK_MORE로 미리
                s.setSuggestionRounds(0);
                s.setState(BotState.ASK_MORE);
                store.save(s);

                // ✅ 로딩 먼저 응답(컨트롤러가 즉시 사용자별 큐로 보내도록)
                ChatMessage[] loading = new ChatMessage[]{
                        bot("""
                            요구사항 요약
                            - 스택: %s
                            - 기간: %s
                            - 인원: %s
                            """.formatted(s.getTechStack(), s.getDuration(), s.getMembers())),
                        bot("잠시만 기다려주세요, 주제를 생성 중입니다... ⏳")
                };

                // ✅ 비동기로 생성 → 사용자별 큐(/user/queue/gpt)로 직접 publish
                final String tech = s.getTechStack();
                final String dur  = s.getDuration();
                final String mem  = s.getMembers();

                botExecutor.execute(() -> {
                    try {
                        String prompt = ProjectIdeaPromptBuilder.buildFirst(tech, dur, mem);
                        String ideas  = gpt.complete(prompt, ProjectIdeaPromptBuilder.systemContext());
                        //몽고DB 요약 저장
                        summaryService.saveFirstIdeas(sessionId, tech, dur, mem, ideas);
                        
                        String combined = """
                        추천 주제 3개:
                        %s

                        더 추천해드릴까요? (네/아니요)
                        """.formatted(ideas);

                        // ★ 사용자별 큐로 송신
                        messagingTemplate.convertAndSendToUser(targetUserKey, "/queue/gpt", bot(combined));
                    } catch (Exception e) {
                        messagingTemplate.convertAndSendToUser(targetUserKey, "/queue/gpt",
                                bot("❗추천 생성 중 오류가 발생했어요. 잠시 후 다시 시도해주세요."));
                    }
                });

                return loading; // 로딩 메시지 즉시 반환
            }

            case ASK_MORE -> {
                if (isYes(userMsg)) {
                    // 추가 추천도 비동기로 처리 (동일 패턴)
                    final BotSession snap = store.getOrCreate(sessionId, userPk);

                    // 준비 중 문구는 즉시(컨트롤러가 사용자별 큐로 보내도록 배열 반환 가능),
                    // 또는 여기서 바로 보내도 됨. 여기서는 바로 보냄.
                    messagingTemplate.convertAndSendToUser(targetUserKey, "/queue/gpt",
                            bot("추가 추천을 준비 중입니다... ⏳"));

                    botExecutor.execute(() -> {
                        try {
                            String prompt = ProjectIdeaPromptBuilder.buildMore(snap);
                            String ideas  = gpt.complete(prompt, ProjectIdeaPromptBuilder.systemContext());
                            //회수 + 1
                            summaryService.extendTtlOnMoreRequest(sessionId);

                            String combined = """
                                    추가 추천 3개:
                                    %s

                                    더 추천해드릴까요? (네/아니요)
                                    """.formatted(ideas);

                            messagingTemplate.convertAndSendToUser(targetUserKey, "/queue/gpt", bot(combined));
                        } catch (Exception e) {
                            messagingTemplate.convertAndSendToUser(targetUserKey, "/queue/gpt",
                                    bot("❗추가 추천 생성 중 오류가 발생했어요."));
                        }
                    });

                    return new ChatMessage[]{}; // 이미 "준비 중"을 보냈으므로 여기서는 빈 배열
                } else if (isNo(userMsg)) {
                    summaryService.markEndedIfExists(sessionId);
                    store.reset(sessionId);
                    return new ChatMessage[]{ bot("도움이 되었다면 좋겠어요! 필요하시면 언제든 다시 '네'라고 시작해 주세요. 👋") };
                } else {
                    return new ChatMessage[]{ bot("답변은 **'네'** 또는 **'아니요'**로 해주세요.") };
                }
            }

            default -> {
                store.reset(sessionId);
                return new ChatMessage[]{ bot("세션이 초기화되었습니다. '네'라고 답해 시작해 주세요!") };
            }
        }
    }

    private boolean isYes(String s) {
        String t = s.toLowerCase();
        return t.equals("네") || t.equals("yes") || t.equals("y");
    }
    private boolean isNo(String s) {
        String t = s.toLowerCase();
        return t.equals("아니요") || t.equals("아니오") || t.equals("no") || t.equals("n");
    }
}