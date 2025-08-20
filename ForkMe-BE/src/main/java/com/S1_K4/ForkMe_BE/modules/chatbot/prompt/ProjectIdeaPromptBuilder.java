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

    // 실무형 멘토 페르소나 + 출력 규격 고정
    private static final String SYSTEM_CTX = """
        당신은 한국어로 답하는 시니어 백엔드 멘토입니다.
        목표: 취업 포트폴리오 심사관이 “실무에 바로 투입 가능”하다고 느낄 만큼 구체적인 프로젝트 주제를 제시.
        원칙:
        - 객체지향(캡슐화·의존역전)과 관심사 분리, 계층화 아키텍처를 전제
        - 실무에서 흔히 쓰는 기술/패턴/운영(테스트·모니터링·배포·보안·성능)을 포함
        - 항목마다 실행 가능한 레벨로 구체적으로 설명(나열 금지)
        출력은 Markdown으로 작성.
        """;

    // 출력 포맷(섹션 강제)
    private static final String FORMAT = """
        ## {번호}. {프로젝트명}
        **한줄 소개**: {한줄소개}

        **핵심 기능 (5~8개)**
        - (도메인 흐름 기준으로 구체적으로 작성)

        **심화 포인트 (실무형)**
        - 성능: (캐싱/쿼리튜닝/부하테스트 등 구체적으로)
        - 보안: (JWT/OAuth2/인가정책 등)
        - 운영: (로깅·모니터링·알림, 장애대응)
        - 확장성/가용성: (스케일아웃/메시징/장애격리)
        - 품질: (테스트 전략/테스트 데이터/CI)

        **기술 스택 매핑**
        - Backend: Spring Boot, Spring MVC, Spring Data JPA, Validation
        - DB: (선정 이유, 핵심 인덱스/스키마 포인트)
        - Cache/Message: (Redis 등, 키/토픽 설계 포함)
        - Search/ETL(선택): (Elasticsearch/Spring Batch 등)
        - Infra: (AWS EC2/RDS/S3, Docker, GitHub Actions 등)
        - Realtime(선택): (WebSocket/STOMP/SSE 중 선택 근거)
        - Monitoring: (Grafana+InfluxDB 또는 ELK, 지표 정의)

        **추천 기간**: (예: %s 기준 합리적 기간)  
        """;

    public String systemContext() {
        return SYSTEM_CTX;
    }

    public static String buildFirst(String techStack, String duration, String members) {
        // BotSession 없이 기존 buildFirst와 동일한 포맷을 그대로 사용
        String system = systemContext();
        String format = /* 기존 FORMAT 상수 그대로 사용 */ FORMAT.formatted(duration);
        return """
            %s

            사용자 요구:
            - 기술스택: %s
            - 예상 기간: %s
            - 예상 인원: %s

            지시사항:
            - 서로 **분야가 다른 3개**의 주제를 제시
            - 아래 "출력 포맷"을 정확히 따를 것
            - 마크다운 구조(제목/볼드/리스트) 유지
            - 너무 포괄적 제목 금지, 구현 가능한 범위로 구체화
            - 스택 매핑에는 "왜 이 기술을 쓰는지" 이유 포함

            출력 포맷(이 틀을 주제별로 반복):
            %s
            """.formatted(system, techStack, duration, members, format);
    }

    public String buildMore(BotSession s) {
        return """
            %s

            이전에 제시했던 3개와 **겹치지 않는 새로운 3개**의 주제를 제시.
            사용자 요구(변함 없음):
            - 기술스택: %s
            - 예상 기간: %s
            - 예상 인원: %s

            지시사항:
            - 동일한 "출력 포맷" 적용
            - 도메인 다양성 유지(이커머스/콘텐츠/실시간/검색/분석 등으로 분산)
            - 채용 관점에서 강점이 드러나게 실무 포인트 강조

            출력 포맷:
            %s
            """.formatted(
                SYSTEM_CTX,
                s.getTechStack(), s.getDuration(), s.getMembers(),
                FORMAT.formatted(s.getDuration())
        );
    }
}