package com.S1_K4.ForkMe_BE.global.common.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;

/**
 * @author : 김관중
 * @packageName : com.S1_K4.ForkMe_BE.global.common.redis
 * @fileName : RedisViewerSessionService
 * @date : 2025-08-04
 * @description : 게시글 뷰어 체크를 위한 세션 서비스
 */

@Service
@RequiredArgsConstructor
public class RedisViewerSessionService {
    private final RedisTemplate<String, String> redisTemplate;

    //세션 정보 저장
    public void saveSessionInfo(String sessionId, Long userPk, Long projectPk){
        String key = getSessionKey(sessionId);
        HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();
        hashOps.put(key, "userPk", userPk.toString());
        hashOps.put(key, "projectPk", projectPk.toString());

        redisTemplate.expire(key, Duration.ofMinutes(3));
    }

    //세션 정보 조회
    public ViewerInfo getSessionInfo(String sessionId) {
        String key = getSessionKey(sessionId);
        HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();
        Map<String, String> data = hashOps.entries(key);
        if(data.isEmpty()) return null;
        try {
            Long userPk = Long.parseLong(data.get("userPk"));
            Long projectPk = Long.parseLong(data.get("projectPk"));
            return new ViewerInfo(userPk, projectPk);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public void deleteSessionInfo(String sessionId){
        redisTemplate.delete(getSessionKey(sessionId));
    }

    private String getSessionKey(String sessionId) {
        return "viewer:session:" + sessionId;
    }

    public record ViewerInfo(Long userPk, Long projectPk) {}
}
