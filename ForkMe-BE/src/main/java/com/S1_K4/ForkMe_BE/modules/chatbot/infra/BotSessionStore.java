package com.S1_K4.ForkMe_BE.modules.chatbot.infra;

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
 * @description : Redis를 이용한 세션관리(TTL)
 */
@RequiredArgsConstructor
@Component
public class BotSessionStore {
    private static final String KEY_PREFIX = "gptbot:session:";
    private static final long TTL_MIN = 30;

    @Qualifier("botSessionRedisTemplate")
    private final RedisTemplate<String, BotSession> botSessionRedisTemplate;

    private String key(String sessionId) { return KEY_PREFIX + sessionId; }

    public BotSession getOrCreate(String sessionId, Long userPk) {
        String k = key(sessionId);
        BotSession s = botSessionRedisTemplate.opsForValue().get(k);
        if (s != null) return s;
        s = BotSession.builder().sessionId(sessionId).userPk(userPk).state(BotState.START).suggestionRounds(0)
                .updatedAt(java.time.Instant.now()).build();
        save(s);
        return s;
    }
    public void save(BotSession s) {
        s.setUpdatedAt(java.time.Instant.now());
        botSessionRedisTemplate.opsForValue().set(key(s.getSessionId()), s, TTL_MIN, java.util.concurrent.TimeUnit.MINUTES);
    }
    public void reset(String sessionId) { botSessionRedisTemplate.delete(key(sessionId)); }
}