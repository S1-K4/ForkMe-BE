package com.S1_K4.ForkMe_BE.global.common.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.global.common.cache
 * @fileName : CacheEvictHelper
 * @date : 2025-08-19
 * @description : 캐시 무효화 헬퍼
 */

@Component
@RequiredArgsConstructor
public class CacheEvictHelper {
    private final CacheManager cacheManager;

    //프로젝트 상세보기, 리스트 캐싱 무효화
    public void evictDetailAndList(Long projectPk) {
        var detail = cacheManager.getCache(CacheNames.PROJECT_DETAIL_STATIC);
        var list   = cacheManager.getCache(CacheNames.PROJECT_LIST);
        detail.evictIfPresent(projectPk);
        list.clear();
    }

    //리스트만 무효화
    public void clearProjectList() {
        var list = cacheManager.getCache(CacheNames.PROJECT_LIST);
        list.clear();
    }

    // 댓글/좋아요 등 상세의 부분 캐시 분리 시
    public void evictPartialOfDetail(Long projectPk) {
        var comments = cacheManager.getCache(CacheNames.PROJECT_COMMENTS);
        var likeCnt  = cacheManager.getCache(CacheNames.PROJECT_LIKE_COUNT);
        comments.evictIfPresent(projectPk);
        likeCnt.evictIfPresent(projectPk);
    }
}