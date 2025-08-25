package com.S1_K4.ForkMe_BE.global.common.cache.project;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.global.common.cache.project
 * @fileName : EvictScope
 * @date : 2025-08-19
 * @description : CacheEvictScope
 */
public enum EvictScope {
    LIST_ONLY,          // 생성 시 목록만 비움
    DETAIL_AND_LIST,    // 수정/삭제/상태변경
    DETAIL_PARTIAL      // 댓글/좋아요 등 부분 캐시
}