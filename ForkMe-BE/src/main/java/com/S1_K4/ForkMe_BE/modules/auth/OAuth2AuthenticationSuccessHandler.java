
package com.S1_K4.ForkMe_BE.modules.auth;

import com.S1_K4.ForkMe_BE.modules.auth.dto.CustomUserDetails;
import com.S1_K4.ForkMe_BE.modules.auth.entity.Auth;
import com.S1_K4.ForkMe_BE.modules.auth.repository.AuthRepository;
import com.S1_K4.ForkMe_BE.modules.auth.service.JwtTokenProvider;
import com.S1_K4.ForkMe_BE.modules.on_project.webhook.GithubHookSessionKeys;
import com.S1_K4.ForkMe_BE.modules.on_project.webhook.client.GithubWebhookClient;
import com.S1_K4.ForkMe_BE.modules.on_project.webhook.dto.PendingHookRequest;
import com.S1_K4.ForkMe_BE.modules.user.entity.User;
import com.S1_K4.ForkMe_BE.modules.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;

import java.io.IOException;
import java.util.Map;


/**
 * @author : 김종국
 * @packageName : com.S1_K4.ForkMe_BE.modules.auth
 * @fileName : OAuth2AuthenticationSuccessHandler
 * @date : 2025-08-04
 * @description : 소셜 로그인 성공 시, JWT를 생성하고 클라이언트에게 전달하는 핸들러
 */


@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthRepository authRepository;

    private final GithubWebhookClient githubWebhookClient;
    private final OAuth2AuthorizedClientService authorizedClientService;

    private final UserService userService;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        HttpSession session = request.getSession(false);
        //세선에 있는 pendinghookrequest를 꺼냄

        if (session == null) {
            log.warn("onAuthSuccess(): session == null");
        } else {
            Object pending = session.getAttribute(GithubHookSessionKeys.PENDING_HOOK);
            Object stateAttr = session.getAttribute("GITHUB_OAUTH_STATE");
            log.info("onAuthSuccess(): sessionId={}, pending={}, GITHUB_OAUTH_STATE={}",
                    session.getId(), pending != null ? "present" : "null", stateAttr);
        }

        String state = request.getParameter("state");
        PendingHookRequest pending = null;

        if (state != null && !state.isBlank()) {
            String key = "pending_hook:" + state;
            try {
                String json = redisTemplate.opsForValue().get(key);
                if (json != null) {
                    pending = objectMapper.readValue(json, PendingHookRequest.class);
                    // 사용 후 삭제(옵션)
                    redisTemplate.delete(key);
                    log.info("onAuthSuccess(): loaded pending from redis for state={}, pending present", state);
                } else {
                    log.warn("onAuthSuccess(): no pending in redis for state={}", state);
                }
            } catch (Exception e) {
                log.error("onAuthSuccess(): failed to read pending from redis for state={}", state, e);
            }
        } else {
            log.info("onAuthSuccess(): state param missing in request");
        }

        //깃헙 권한 인증후 받아온 깃헙 토큰을 가져옴
        OAuth2AuthenticationToken oauth2 =
                (authentication instanceof OAuth2AuthenticationToken)
                    ? (OAuth2AuthenticationToken) authentication : null;

        if (oauth2 != null) {
            log.info("onAuthSuccess(): authorizedClientRegistrationId={}", oauth2.getAuthorizedClientRegistrationId());
        }

        if(pending != null && oauth2 != null
                && "github-hooks".equals(oauth2.getAuthorizedClientRegistrationId())) {

            String principalName = oauth2.getName();
            String redirectUrl;

            try {
                OAuth2AuthorizedClient client =
                        authorizedClientService.loadAuthorizedClient(
                                oauth2.getAuthorizedClientRegistrationId(), principalName);
                if(client == null || client.getAccessToken() == null){
                    throw new IllegalStateException("token is missing");
                }

                String ghAccessToken = client.getAccessToken().getTokenValue();
                boolean insecure = Boolean.TRUE.equals(pending.insecureSsl());
                String secret = pending.overrideSecret();

                //훅 정보 생성
                Map<String, Object> hook;
                if("repo".equals(pending.mode())) {
                    hook = githubWebhookClient.createRepoWebhook(
                            ghAccessToken, pending.owner(), pending.repo(),
                            pending.events(), secret, insecure, pending.projectPk());
                } else {
                    hook = githubWebhookClient.createOrgWebhook(
                            ghAccessToken, pending.owner(), pending.events(), secret, insecure, pending.projectPk()
                    );
                }

                Object id = (hook != null) ? hook.get("id") : null;
                redirectUrl = UriComponentsBuilder.fromPath("/hook-result.html")
                        .queryParam("ok", 1)
                        .queryParam("hookId", id)
                        .build().toUriString();
            } catch (Exception ex) {
                log.error("Webhook creation failed: {}", ex.getMessage(), ex);
                redirectUrl = UriComponentsBuilder.fromPath("/hook-result.html")
                        .queryParam("ok", 0)
                        .queryParam("error", ex.getMessage())
                        .build().toUriString();
            } finally {
                try {
                    authorizedClientService.removeAuthorizedClient(
                            oauth2.getAuthorizedClientRegistrationId(), principalName);
                } catch (Throwable ignore) {}

                if (state != null && !state.isBlank()) {
                    redisTemplate.delete("pending_hook:" + state);
                }
            }

            getRedirectStrategy().sendRedirect(request, response, redirectUrl);
            return;
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        String accessToken = jwtTokenProvider.generateAccessToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);

        Auth auth = authRepository.findByUser(user)
                .orElse(new Auth(user));
        auth.updateAccessToken(accessToken);
        auth.updateRefreshToken(refreshToken);
        authRepository.save(auth);


        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        response.addCookie(refreshTokenCookie);

        String url = "http://forkme.site:3000/login-success.html";
        //String url = "http://localhost:3000/login-success.html";
//        String url = "http://localhost:8080/chatting-test22.html"; //채팅 테스트(남이)
        String targetUrl = UriComponentsBuilder.fromUriString(url)
                .queryParam("accessToken", accessToken)
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);

    }
}
