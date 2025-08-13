package com.S1_K4.ForkMe_BE.modules.chatting.presence.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @author : 김남이
 * @packageName : com.S1_K4.ForkMe_BE.modules.chatting.presence.service
 * @fileName : ChattingPresenceServiceImpl
 * @date : 2025-08-12
 * @description : 채팅에서 접속자 현황을 관리하는 서비스 구현
 */
@Service
@RequiredArgsConstructor
public class ChattingPresenceServiceImpl implements ChattingPresenceService {

    @Qualifier("jsonRedisTemplate")
    private final RedisTemplate<String, Object> jsonRedisTemplate;

    private String roomOnlineKey(Long roomPk)      { return "chat:online:" + roomPk; }
    private String userSessionsKey(Long r, Long u) { return "chat:userSessions:" + r + ":" + u; }
    private String sessionHashKey(String s)        { return "chat:session:" + s; }

    @Override
    public void onSubscribe(String sessionId, Long roomPk, Long userPk) {
        jsonRedisTemplate.opsForHash().put(sessionHashKey(sessionId), "roomPk", roomPk.toString());
        jsonRedisTemplate.opsForHash().put(sessionHashKey(sessionId), "userPk", userPk.toString());
        jsonRedisTemplate.opsForSet().add(userSessionsKey(roomPk, userPk), sessionId);
        jsonRedisTemplate.opsForSet().add(roomOnlineKey(roomPk), userPk.toString());
    }

    @Override
    public Long onUnsubscribeOrDisconnect(String sessionId) { // [변경] 반환형 Long
        var hKey = sessionHashKey(sessionId);
        Object roomPkObj = jsonRedisTemplate.opsForHash().get(hKey, "roomPk");
        Object userPkObj = jsonRedisTemplate.opsForHash().get(hKey, "userPk");
        if (roomPkObj == null || userPkObj == null) return null; // [변경]

        Long roomPk = Long.parseLong(roomPkObj.toString());
        Long userPk = Long.parseLong(userPkObj.toString());

        jsonRedisTemplate.opsForSet().remove(userSessionsKey(roomPk, userPk), sessionId);
        Long remain = jsonRedisTemplate.opsForSet().size(userSessionsKey(roomPk, userPk));
        if (remain == null || remain == 0) {
            jsonRedisTemplate.opsForSet().remove(roomOnlineKey(roomPk), userPk.toString());
        }
        jsonRedisTemplate.delete(hKey);
        return roomPk; // [추가] 정리한 roomPk를 돌려줌 → 브로드캐스트에 사용
    }

    @Override
    public boolean isOnline(Long roomPk, Long userPk) {
        Boolean member = jsonRedisTemplate.opsForSet()
                .isMember(roomOnlineKey(roomPk), userPk.toString());
        return Boolean.TRUE.equals(member);
    }
}

