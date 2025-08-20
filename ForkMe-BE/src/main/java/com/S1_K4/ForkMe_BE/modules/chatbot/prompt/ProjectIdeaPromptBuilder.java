package com.S1_K4.ForkMe_BE.modules.chatbot.prompt;

import com.S1_K4.ForkMe_BE.modules.chatbot.domain.BotSession;
import lombok.experimental.UtilityClass;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.chatbot.prompt
 * @fileName : ProjectIdeaPromptBuilder
 * @date : 2025-08-20
 * @description : 프롬프트 빌더
 */
@UtilityClass
public class ProjectIdeaPromptBuilder {

    public String buildFirst(BotSession s) {
        return """
                당신은 개발자 취업용 포트폴리오에 적합한 '팀 프로젝트 주제 추천' 도우미입니다.
                제약:
                - 기술스택: %s
                - 예상 기간: %s
                - 예상 인원: %s
                출력 형식(한국어, 간결):
                1) <프로젝트명>
                   - 한줄 소개:
                   - 핵심 기능(3~5개, 불릿):
                   - 왜 적합한가:
                   - 예상 난이도: (하/중/상)

                위와 같은 형태로 **서로 다른 3개**를 제시하세요.
                """.formatted(s.getTechStack(), s.getDuration(), s.getMembers());
    }

    public String buildMore(BotSession s) {
        return """
                이전과 동일한 제약으로, **기존과 겹치지 않는 새로운 3개**를 제시하세요.
                기술스택: %s / 기간: %s / 인원: %s
                같은 출력 형식을 유지하세요.
                """.formatted(s.getTechStack(), s.getDuration(), s.getMembers());
    }
}