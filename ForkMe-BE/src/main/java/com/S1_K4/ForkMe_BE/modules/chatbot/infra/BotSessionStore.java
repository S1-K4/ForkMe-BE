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
@Component
@RequiredArgsConstructor
public class BotSessionStore {

    private static final String KEY_PREFIX = "gptbot:session:";
    private static final long TTL_MIN = 30; // 30분 세션 TTL

    @Qualifier("jsonRedisTemplate")
    private final RedisTemplate<String, Object> jsonRedisTemplate;

    private String key(String sessionId) { return KEY_PREFIX + sessionId; }

    public BotSession getOrCreate(String sessionId, Long userPk) {
        String key = key(sessionId);
        Object val = jsonRedisTemplate.opsForValue().get(key);
        if (val instanceof BotSession s) {
            return s;
        }
        BotSession created = BotSession.builder()
                .sessionId(sessionId)
                .userPk(userPk)
                .state(BotState.START)
                .suggestionRounds(0)
                .updatedAt(Instant.now())
                .build();
        save(created);
        return created;
    }

    public void save(BotSession session) {
        session.setUpdatedAt(Instant.now());
        jsonRedisTemplate.opsForValue().set(key(session.getSessionId()), session, TTL_MIN, TimeUnit.MINUTES);
    }

    public void reset(String sessionId) {
        jsonRedisTemplate.delete(key(sessionId));
    }
}