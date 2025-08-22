
package com.S1_K4.ForkMe_BE.modules.on_project.webhook.controller;

import com.S1_K4.ForkMe_BE.modules.on_project.webhook.GithubHookSessionKeys;
import com.S1_K4.ForkMe_BE.modules.on_project.webhook.dto.GithubEventResponseDto;
import com.S1_K4.ForkMe_BE.modules.on_project.webhook.dto.PendingHookRequest;
import com.S1_K4.ForkMe_BE.modules.on_project.webhook.service.WebhookService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;


import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/*
 * @author : 김관중
 * @packageName : com.S1_K4.ForkMe_BE.modules.on_project.webhook.controller
 * @fileName : WebhookController
 * @date : 2025-08-08
 * @description : 깃허브 웹훅 컨트롤러입니다.
 */

@Slf4j
@RestController
@RequestMapping("/api/github")
@RequiredArgsConstructor
public class WebhookController {

    @Value("${github.webhook.secret}")
    private String secret;

    @Value("${spring.security.oauth2.client.registration.github-hooks.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.github-hooks.redirect-uri}")
    private String fixedRedirectUri;

    private final StringRedisTemplate redisTemplate;
    private final WebhookService webhookService;
    private final ObjectMapper om = new ObjectMapper();

    @GetMapping("/hooks/authorize")
    public RedirectView authorize(
            HttpSession session,
            @RequestParam String mode,                // "repo" | "org"
            @RequestParam String owner,
            @RequestParam(required = false) String repo,
            @RequestParam(required = false, defaultValue = "push") String events,
            @RequestParam(required = false, defaultValue = "false") boolean insecureSsl,
            @RequestParam(required = false) String overrideSecret,
            @RequestParam(required = false) Long projectPk
    ) {
        //모드 설정 없는 경우 체크
        if (!"repo".equals(mode) && !"org".equals(mode)) {
            throw new IllegalArgumentException("mode must be 'repo' or 'org'");
        }
        //repo만 연결할경우 repo 값 필요
        if ("repo".equals(mode) && !StringUtils.hasText(repo)) {
            throw new IllegalArgumentException("repo is required when mode=repo");
        }

        //가져오는 이벤트 리스트 설정
        List<String> eventList = "*".equals(events)
                ? List.of("*")
                : Arrays.stream(events.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        PendingHookRequest pending = new PendingHookRequest(mode, owner, repo, eventList, insecureSsl, overrideSecret, projectPk);

        // 1) state 생성
        String state = java.util.UUID.randomUUID().toString();

        // 2) pending을 JSON으로 직렬화해서 Redis에 저장 (TTL 10분)
        try {
            String json = om.writeValueAsString(pending);
            String key = "pending_hook:" + state;
            redisTemplate.opsForValue().set(key, json, 10, TimeUnit.MINUTES);
        } catch (Exception e) {
            throw new RuntimeException("failed to save pending hook to redis", e);
        }

        String scope = "admin:repo_hook admin:org_hook read:org repo";
        String githubUrl = "https://github.com/login/oauth/authorize"
                + "?client_id=" + URLEncoder.encode(clientId, StandardCharsets.UTF_8)
                + "&scope=" + URLEncoder.encode(scope, StandardCharsets.UTF_8)
                + "&state=" + URLEncoder.encode(state, StandardCharsets.UTF_8)
                + "&redirect_uri=" + URLEncoder.encode(fixedRedirectUri, StandardCharsets.UTF_8);

        log.info("authorize(): state={}, redisKey=pending_hook:{}", state, state);
        // 등록한 registrationId와 정확히 일치해야 함. 아래 주소로 리다이렉트(깃헙훅 OAuth2 체크)
        return new RedirectView(githubUrl);
        //리다이렉트 화면에서 깃헙 권한체크 화면
        //Oauth2authenticationSuccessHandler 으로 이동.
    }

    @PostMapping("/webhooks")
    public ResponseEntity<?> receive(
            @RequestParam(required = false) Long projectPk,
            @RequestHeader(value="X-Hub-Signature-256", required = false)String sig256,
            @RequestHeader(value="X-GitHub-Event", required = false)String eventType,
            @RequestHeader(value="X-GitHub-Delivery", required = false)String deliveryId,
            @RequestBody byte[] bodyBytes,
            HttpServletRequest request) throws Exception {

        if("ping".equals(eventType)) {
            return ResponseEntity.ok().build();
        }

        if(StringUtils.hasText(secret)) {
            if(!StringUtils.hasText(sig256) || !sig256.startsWith("sha256=")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("signature missing");
            }

            String expected = "sha256="+hmacSha256Hex(secret, bodyBytes);
            if(!MessageDigest.isEqual(expected.getBytes(StandardCharsets.UTF_8),
                    sig256.getBytes(StandardCharsets.UTF_8))) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("signature missmatch");
            }
        }

        JsonNode payload = om.readTree(bodyBytes);

        webhookService.recordTimeline(projectPk,eventType,deliveryId,payload);

        return ResponseEntity.ok("ok");
    }

    private static String hmacSha256Hex(String secret, byte[] body) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKey key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(key);
        byte[] raw = mac.doFinal(body);
        StringBuilder sb = new StringBuilder(raw.length*2);
        for (byte b : raw) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    @GetMapping("{projectPk}/github-events")
    public ResponseEntity<?> getGithubEvents(
            @PathVariable("projectPk") Long projectPk,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        List<GithubEventResponseDto> events = webhookService.getEventsByProject(projectPk, page, size);
        boolean isConnected = webhookService.isWebhookConnected(projectPk);

        Map<String, Object> data = new HashMap<>();
        data.put("isWebhookConnected", isConnected);
        data.put("events", events);

        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("code", 200);
        resp.put("message", "GitHub 이벤트 조회 성공");
        resp.put("data", data);

        return ResponseEntity.ok(resp);
    }
}

