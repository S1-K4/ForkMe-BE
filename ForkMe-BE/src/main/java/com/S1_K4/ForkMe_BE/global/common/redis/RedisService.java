package com.S1_K4.ForkMe_BE.global.common.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;

/**
 * @author : 김관중
 * @packageName : com.S1_K4.ForkMe_BE.global.common.redis
 * @fileName : RedisService
 * @date : 2025-08-04
 * @description : redis 사용을 위한 service 입니다
 */

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    //게시글 조회 key 생성
    private String getPostViewersKey(Long projectPk) {
        return "viewers:post:"+projectPk;
    }

    //게시글 입장
    public void enterPost(Long projectPk,Long userPk) {
        SetOperations<String, String> setOps = redisTemplate.opsForSet();
        setOps.add(getPostViewersKey(projectPk), userPk.toString());
    }

    //게시글 퇴장
    public void leavePost(Long projectPk,Long userPk) {
        SetOperations<String, String> setOps = redisTemplate.opsForSet();
        setOps.remove(getPostViewersKey(projectPk), userPk.toString());
    }

    //현재 접속자 수 조회
    public long getViewerCount(Long projectPk) {
        Long count = redisTemplate.opsForSet().size(getPostViewersKey(projectPk));
        return count != null ? count : 0L;
    }

    //해당 유저가 보고 있는지 확인
    public boolean isViewing(Long projectPk,Long userPk) {
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(getPostViewersKey(projectPk), userPk.toString()));
    }

    public RedisTemplate<String, String> getTemplate() {
        return redisTemplate;
    }
}
