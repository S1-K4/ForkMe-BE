package com.S1_K4.ForkMe_BE.global.common.cache.project;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.global.common.cache
 * @fileName : ProjectCacheEvictEvent
 * @date : 2025-08-19
 * @description : 프로젝트 캐시 이벤트
 */
public record ProjectCacheEvictEvent(Long projectPk, EvictScope scope) {}
