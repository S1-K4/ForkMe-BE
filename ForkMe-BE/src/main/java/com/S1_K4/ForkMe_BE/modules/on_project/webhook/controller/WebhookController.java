/*
package com.S1_K4.ForkMe_BE.modules.on_project.webhook.controller;

import com.S1_K4.ForkMe_BE.modules.on_project.webhook.GithubHookSessionKeys;
import com.S1_K4.ForkMe_BE.modules.on_project.webhook.dto.PendingHookRequest;
import com.S1_K4.ForkMe_BE.modules.on_project.webhook.service.WebhookService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.List;

*/
/**
 * @author : 김관중
 * @packageName : com.S1_K4.ForkMe_BE.modules.on_project.webhook.controller
 * @fileName : WebhookController
 * @date : 2025-08-08
 * @description : 깃허브 웹훅 컨트롤러입니다.
 *//*

@RestController
@RequestMapping("/api/github")
@RequiredArgsConstructor
public class WebhookController {

    @Value("${github.webhook.secret}")
    private String secret;

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
            @RequestParam(required = false) String overrideSecret
    ) {
        if (!"repo".equals(mode) && !"org".equals(mode)) {
            throw new IllegalArgumentException("mode must be 'repo' or 'org'");
        }
        if ("repo".equals(mode) && !StringUtils.hasText(repo)) {
            throw new IllegalArgumentException("repo is required when mode=repo");
        }

        List<String> eventList = "*".equals(events)
                ? List.of("*")
                : Arrays.stream(events.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        session.setAttribute(
                GithubHookSessionKeys.PENDING_HOOK,
                new PendingHookRequest(mode, owner, repo, eventList, insecureSsl, overrideSecret)
        );

        // 등록한 registrationId와 정확히 일치해야 함
        return new RedirectView("/oauth2/authorization/github-hooks?prompt=consent");
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

}
*/
