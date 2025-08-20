package com.S1_K4.ForkMe_BE.modules.chatbot.app;

import com.S1_K4.ForkMe_BE.modules.chatbot.domain.BotSession;
import com.S1_K4.ForkMe_BE.modules.chatbot.domain.BotState;
import com.S1_K4.ForkMe_BE.modules.chatbot.dto.ChatMessage;
import com.S1_K4.ForkMe_BE.modules.chatbot.infra.BotSessionStore;
import com.S1_K4.ForkMe_BE.modules.chatbot.llm.GPTService;
import com.S1_K4.ForkMe_BE.modules.chatbot.prompt.ProjectIdeaPromptBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.chatbot.app
 * @fileName : ProjectTopicBotService
 * @date : 2025-08-20
 * @description : projectTopicBotService
 */
@Service
@RequiredArgsConstructor
public class ProjectTopicBotService {

    private final BotSessionStore store;
    private final GPTService gpt;

    private ChatMessage bot(String text) { return new ChatMessage("GPT-봇", text); }
    private ChatMessage userEcho(String text) { return new ChatMessage("나", text); }

    public ChatMessage[] handle(String sessionId, Long userPk, String incomingText) {
        BotSession s = store.getOrCreate(sessionId, userPk);
        String userMsg = incomingText == null ? "" : incomingText.trim();

        switch (s.getState()) {
            case START -> {
                s.setState(BotState.AWAIT_YES);
                store.save(s);
                return new ChatMessage[] {
                        bot("안녕하세요! 저는 **프로젝트 주제추천 챗봇**이에요. 주제추천을 해드릴까요? (답변: 네)")
                };
            }
            case AWAIT_YES -> {
                if (isYes(userMsg)) {
                    s.setState(BotState.ASK_STACK);
                    store.save(s);
                    return new ChatMessage[] {
                            userEcho(userMsg),
                            bot("좋아요! 먼저 **프로젝트 기술스택**을 알려주세요. (예: Java, Spring, Redis)")
                    };
                } else {
                    // "네"만 허용
                    return new ChatMessage[] {
                            userEcho(userMsg),
                            bot("죄송해요, 시작하려면 '네'라고 입력해주세요.")
                    };
                }
            }
            case ASK_STACK -> {
                if (!StringUtils.hasText(userMsg)) {
                    return new ChatMessage[] { bot("기술스택을 입력해주세요. (예: Java, Spring, Redis)") };
                }
                s.setTechStack(userMsg);
                s.setState(BotState.ASK_DURATION);
                store.save(s);
                return new ChatMessage[] {
                        userEcho(userMsg),
                        bot("다음으로 **예상 기간**을 알려주세요! (예: 1개월 / 6주 / 2개월)")
                };
            }
            case ASK_DURATION -> {
                if (!StringUtils.hasText(userMsg)) {
                    return new ChatMessage[] { bot("예상 기간을 입력해주세요. (예: 2개월)") };
                }
                s.setDuration(userMsg);
                s.setState(BotState.ASK_MEMBERS);
                store.save(s);
                return new ChatMessage[] {
                        userEcho(userMsg),
                        bot("마지막으로 **예상 인원**을 알려주세요! (예: 3명)")
                };
            }
            case ASK_MEMBERS -> {
                if (!StringUtils.hasText(userMsg)) {
                    return new ChatMessage[] { bot("예상 인원을 입력해주세요. (예: 3명)") };
                }
                s.setMembers(userMsg);
                s.setState(BotState.READY_TO_SUGGEST);
                store.save(s);
                // 즉시 추천 3개
                String prompt = ProjectIdeaPromptBuilder.buildFirst(s);
                String ideas = gpt.complete(prompt);
                s.setSuggestionRounds(1);
                s.setState(BotState.ASK_MORE);
                store.save(s);
                return new ChatMessage[] {
                        userEcho(userMsg),
                        bot("요구사항 요약\n- 스택: %s\n- 기간: %s\n- 인원: %s".formatted(s.getTechStack(), s.getDuration(), s.getMembers())),
                        bot("추천 주제 3개:\n" + ideas),
                        bot("더 추천해드릴까요? (네/아니요)")
                };
            }
            case ASK_MORE -> {
                if (isYes(userMsg)) {
                    // 추가 3개
                    String prompt = ProjectIdeaPromptBuilder.buildMore(s);
                    String ideas = gpt.complete(prompt);
                    s.setSuggestionRounds(s.getSuggestionRounds() + 1);
                    store.save(s);
                    return new ChatMessage[] {
                            userEcho(userMsg),
                            bot("추가 추천 3개:\n" + ideas),
                            bot("더 추천해드릴까요? (네/아니요)")
                    };
                } else if (isNo(userMsg)) {
                    store.reset(sessionId);
                    return new ChatMessage[] {
                            userEcho(userMsg),
                            bot("도움이 되었다면 좋겠어요! 필요하시면 언제든 다시 '네'라고 시작해 주세요. 👋")
                    };
                } else {
                    return new ChatMessage[] {
                            userEcho(userMsg),
                            bot("답변은 '네' 또는 '아니요'로 해주세요.")
                    };
                }
            }
            default -> {
                store.reset(sessionId);
                return new ChatMessage[] { bot("세션이 초기화되었습니다. '네'라고 답해 시작해 주세요!") };
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
