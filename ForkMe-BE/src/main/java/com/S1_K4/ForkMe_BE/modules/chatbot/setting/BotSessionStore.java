package com.S1_K4.ForkMe_BE.modules.chatbot.setting;

import com.S1_K4.ForkMe_BE.modules.chatbot.domain.BotSession;
import com.S1_K4.ForkMe_BE.modules.chatbot.domain.BotState;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.chatbot.infra
 * @fileName : BotSessionStore
 * @date : 2025-08-20
 * @description : Redis를 이용한 세션관리(TTL) -> BotSession을 가져오고 저장, 초기화하는 세션 저장소.
 *                 Redis에 gptbot:session:{sessionId}키로 저장하고 TTL줌
 */
@RequiredArgsConstructor
@Component
public class BotSessionStore {
    private static final String KEY_PREFIX = "gptbot:session:";
    private static final long TTL_MIN = 30; //TTL:30분. 30분동안 대화가 없으면 redis가 자동으로 세션 삭제

    //JSON으로 직렬화해 저장하는 템플릿
    @Qualifier("botSessionRedisTemplate")
    private final RedisTemplate<String, BotSession> botSessionRedisTemplate;

    //세션 ID -> REDIS 키 문자열로 변경
    private String key(String sessionId) { return KEY_PREFIX + sessionId; } //gptbot:session:{sessionId}

    //세션이 있으면 가져오고, 없으면 생성하는 메서드
    public BotSession getOrCreate(String sessionId, Long userPk) {
        String k = key(sessionId);
        BotSession s = botSessionRedisTemplate.opsForValue().get(k);
        if (s != null) return s;

        //없으면 START상태로 새 세션을 만들어놓고 반환
        s = BotSession.builder().sessionId(sessionId).userPk(userPk).state(BotState.START).suggestionRounds(0)
                .updatedAt(java.time.Instant.now()).build();
        save(s);
        return s;
    }
    //상태나 입력값 변동 시 즉시 저장(TTL 갱신)
    public void save(BotSession s) {
        s.setUpdatedAt(java.time.Instant.now());
        botSessionRedisTemplate.opsForValue().set(key(s.getSessionId()), s, TTL_MIN, java.util.concurrent.TimeUnit.MINUTES);
    }

    //세션 삭제
    public void reset(String sessionId) { botSessionRedisTemplate.delete(key(sessionId)); }
}