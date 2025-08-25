package com.S1_K4.ForkMe_BE.global.common.cache.project;

import com.S1_K4.ForkMe_BE.global.common.cache.CacheEvictHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.global.common.cache
 * @fileName : ProjectCacheEvictListener
 * @date : 2025-08-19
 * @description : 캐시 무효화 리스너
 */
@Component
@RequiredArgsConstructor
public class ProjectCacheEvictListener {

    private final CacheEvictHelper cacheEvictHelper;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onProjectChanged(ProjectCacheEvictEvent e) {
        switch (e.scope()) {
            case LIST_ONLY -> cacheEvictHelper.clearProjectList();
            case DETAIL_AND_LIST -> cacheEvictHelper.evictDetailAndList(e.projectPk());
            case DETAIL_PARTIAL -> cacheEvictHelper.evictPartialOfDetail(e.projectPk()); // 필요 시 구현
        }
    }
}