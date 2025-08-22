package com.S1_K4.ForkMe_BE.modules.chatbot.service;

import com.S1_K4.ForkMe_BE.modules.chatbot.domain.ChatSessionSummary;
import com.S1_K4.ForkMe_BE.modules.chatbot.repository.ChatSessionSummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class ChatSessionSummaryService {

    private final ChatSessionSummaryRepository repo;

    /** 보존 기간 : 30일 */
    private static final long RETENTION_DAYS = 30;

    private Date ttlExpireAtFromNow(long days) {
        return Date.from(Instant.now().plus(days, ChronoUnit.DAYS));
    }

    /**
     * 마크다운에서 주제 제목(최대 3개)을 뽑아 리스트로 변환
     * - "## 1. 제목" / "## 제목" 형태 모두 지원
     */
    private List<String> extractIdeaTitles(String md) {
        List<String> titles = new ArrayList<>();
        if (md == null || md.isBlank()) return titles;

        // 1) "## ..." 헤딩 우선 파싱
        //    - "## 1. 실시간 이슈 ..." 처럼 앞에 번호가 붙어도 OK
        Pattern h2 = Pattern.compile("^##\\s+(?:\\d+\\.\\s*)?(.+)$", Pattern.MULTILINE);
        Matcher m = h2.matcher(md);
        while (m.find() && titles.size() < 3) {
            String t = m.group(1).trim();
            // 짝지어지는 설명 콜론/괄호 앞까지만 간단 스플릿 (너무 길면 잘라주기)
            t = t.replaceAll("\\s+", " ");
            titles.add(t);
        }

        // 2) 헤딩이 없다면, 백업 전략: 굵은 제목 라인이나 리스트 첫 줄 등에서 최대 3개 추정
        if (titles.isEmpty()) {
            // 대강 첫 3줄 중 빈 줄 제외 후 넣기
            String[] lines = md.split("\\r?\\n");
            for (String line : lines) {
                String s = line.strip();
                if (s.isEmpty()) continue;
                // 너무 긴 라인은 스킵
                if (s.length() > 120) continue;
                // 마크다운 기호 제거
                s = s.replaceAll("^[-*+]\\s*", "")
                        .replaceAll("^\\*\\*|\\*\\*$", "")
                        .replaceAll("^#+\\s*", "")
                        .trim();
                if (!s.isEmpty()) {
                    titles.add(s);
                }
                if (titles.size() >= 3) break;
            }
        }

        return titles;
    }

    /** 첫 추천이 생성되었을 때 저장(없으면 생성, 있으면 갱신) */
    @Async("botExecutor")
    public void saveFirstIdeas(String sessionId,
                               String tech, String dur, String mem,
                               String ideasMd) {

        Instant now = Instant.now();
        Date expires = ttlExpireAtFromNow(RETENTION_DAYS);

        ChatSessionSummary doc = repo.findBySessionId(sessionId).orElseGet(() -> {
            ChatSessionSummary created = new ChatSessionSummary();
            created.setSessionId(sessionId);
            created.setStartedAt(now);
            created.setExpiresAt(expires);
            return created;
        });

        doc.setTechStack(tech);
        doc.setDuration(dur);
        doc.setMembers(mem);
        doc.setFirstIdeasAt(now);
        doc.setIdeas(extractIdeaTitles(ideasMd));
        // TTL 갱신(첫 저장 시점 기준 30일 유지)
        doc.setExpiresAt(expires);

        repo.save(doc);
    }

    /**
     * 추가 추천 요청 시: TTL만 연장(요약 정책상 카운트 저장 안 함)
     * 필요하면 여기에서 카운트 필드 추가 후 증가하도록 확장 가능.
     */
    @Async("botExecutor")
    public void extendTtlOnMoreRequest(String sessionId) {
        repo.findBySessionId(sessionId).ifPresent(doc -> {
            doc.setExpiresAt(ttlExpireAtFromNow(RETENTION_DAYS));
            repo.save(doc);
        });
    }

    /** 사용자가 종료(아니요) 응답 → 종료 시각 기록(멱등) */
    @Async("botExecutor")
    public void markEndedIfExists(String sessionId) {
        repo.findBySessionId(sessionId).ifPresent(doc -> {
            if (doc.getEndedAt() == null) {
                doc.setEndedAt(Instant.now());
                repo.save(doc);
            }
        });
    }
}