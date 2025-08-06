package com.S1_K4.ForkMe_BE.global.common.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * @author : 김관중
 * @packageName : com.S1_K4.ForkMe_BE.global.common.redis
 * @fileName : ViewerBroadcaster
 * @date : 2025-08-04
 * @description : 게시글 뷰어수를 전달해주는 클래스
 */
@Service
@RequiredArgsConstructor
public class ViewerBroadcaster {
    private final RedisService redisService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public void broadcastViewerCount(Long projectPk) {
        long count = redisService.getViewerCount(projectPk);
        simpMessagingTemplate.convertAndSend("/sub/post/" + projectPk + "/viewers", count);
    }
}
