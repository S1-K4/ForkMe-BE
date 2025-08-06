package com.S1_K4.ForkMe_BE.global.common.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author : 김관중
 * @packageName : com.S1_K4.ForkMe_BE.global.common.redis
 * @fileName : RedisTestController
 * @date : 2025-08-05
 * @description : Redis 접속 테스트 컨트롤러 입니다
 */

@RestController
@RequestMapping("/api/redis")
@RequiredArgsConstructor
public class RedisTestController {

    private final RedisService redisService;
    private final ViewerBroadcaster viewerBroadcaster;
    private final RedisViewerSessionService sessionService;

    @PostMapping("/enter")
    public void simulateConnect(
            @RequestParam String sessionId,
            @RequestParam Long userPk,
            @RequestParam Long projectPk) {
        sessionService.saveSessionInfo(sessionId, userPk, projectPk);
        redisService.enterPost(projectPk, userPk);
        viewerBroadcaster.broadcastViewerCount(projectPk);
    }

    @PostMapping("/leave")
    public void simulateDisconnect(@RequestParam String sessionId) {
        RedisViewerSessionService.ViewerInfo info = sessionService.getSessionInfo(sessionId);
        if (info != null) {
            redisService.leavePost(info.projectPk(), info.userPk());
            viewerBroadcaster.broadcastViewerCount(info.projectPk());
            sessionService.deleteSessionInfo(sessionId);
        }
    }

    @GetMapping("/viewers/{postId}")
    public long getViewerCount(@PathVariable Long postId) {
        return redisService.getViewerCount(postId);
    }

    @GetMapping("/session/{sessionId}")
    public ResponseEntity<Map<String, String>> getSessionInfo(@PathVariable String sessionId) {
        Map<Object, Object> raw = redisService.getTemplate().opsForHash().entries("viewer:session:" + sessionId);
        Map<String, String> result = raw.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> e.getKey().toString(),
                        e -> e.getValue().toString()
                ));
        return ResponseEntity.ok(result);
    }
}
