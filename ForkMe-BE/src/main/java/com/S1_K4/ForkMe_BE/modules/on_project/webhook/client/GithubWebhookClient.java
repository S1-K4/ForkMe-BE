/*
package com.S1_K4.ForkMe_BE.modules.on_project.webhook.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.HttpHeaders;

import java.util.List;
import java.util.Map;

*/
/**
 * @author : 김관중
 * @packageName : com.S1_K4.ForkMe_BE.modules.on_project.webhook.client
 * @fileName : GithubWebhookClient
 * @date : 2025-08-10
 * @description : 웹훅 호출 클라이언트 입니다
 *//*


@Component
@RequiredArgsConstructor
public class GithubWebhookClient {

    @Value("${github.api.base}")
    private String apiBase;
    @Value("${github.webhook.callback-url}")
    private String callbackUrl;
    @Value("${github.webhook.secret}")
    private String defaultSecret;
    @Value("${github.webhook.allow-insecure-ssl:false}")
    private boolean allowInsecureSslDefault;

    private WebClient client(String token) {
        return WebClient.builder()
                .baseUrl(apiBase)
                .defaultHeader(HttpHeaders.ACCEPT, "application/vnd.github+json")
                .defaultHeader("X-GitHub-Api-Version","2022-11-28")
                .defaultHeader(HttpHeaders.USER_AGENT, "ForkMe/1.0")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer "+token)
                .build();
    }

    public Map<String, Object> createRepoWebhook(
            String token, String owner, String repo,
            List<String> events, String secret, boolean insecureSsl
    ) {
        if(events == null || events.isEmpty()) events = List.of("push");
        String useSecret = (secret == null || secret.isBlank()) ? defaultSecret : secret;

        Map<String, Object> body = Map.of(
                "name","web","active",true, "events", events,
                "config", Map.of(
                        "url", callbackUrl,
                        "contentType", "json",
                        "secret", useSecret,
                        "insecure_ssl", (insecureSsl ? "1" : (allowInsecureSslDefault ? "1" : "0"))
                )
        );

        return client(token).post()
                .uri("/repos/{owner}/{repo}/hooks", owner, repo)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();
    }

    public Map<String,Object> createOrgWebhook(
            String token, String org, List<String> events, String secret, boolean insecureSsl
    ){
        if(events == null || events.isEmpty()) events = List.of("push");
        String useSecret = (secret == null || secret.isBlank()) ? defaultSecret : secret;

        Map<String, Object> body = Map.of(
                "name","web","active",true, "events", events,
                "config", Map.of(
                        "url", callbackUrl,
                        "contentType", "json",
                        "secret", useSecret,
                        "insecure_ssl", (insecureSsl ? "1" : (allowInsecureSslDefault ? "1" : "0"))
                )
        );

        return client(token).post()
                .uri("/orgs/{org}/hooks", org)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .block();
    }
}
*/
