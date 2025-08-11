
package com.S1_K4.ForkMe_BE.modules.auth;

import com.S1_K4.ForkMe_BE.modules.auth.dto.CustomUserDetails;
import com.S1_K4.ForkMe_BE.modules.auth.entity.Auth;
import com.S1_K4.ForkMe_BE.modules.auth.repository.AuthRepository;
import com.S1_K4.ForkMe_BE.modules.auth.service.JwtTokenProvider;
import com.S1_K4.ForkMe_BE.modules.on_project.webhook.GithubHookSessionKeys;
import com.S1_K4.ForkMe_BE.modules.on_project.webhook.client.GithubWebhookClient;
import com.S1_K4.ForkMe_BE.modules.on_project.webhook.dto.PendingHookRequest;
import com.S1_K4.ForkMe_BE.modules.user.entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
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
/*
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthRepository authRepository;

    private final GithubWebhookClient githubWebhookClient;
    private final OAuth2AuthorizedClientService authorizedClientService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        HttpSession session = request.getSession(false);
        //세선에 있는 pendinghookrequest를 꺼냄
        PendingHookRequest pending = (session != null)
                ? (PendingHookRequest) session.getAttribute(GithubHookSessionKeys.PENDING_HOOK) : null;

        //깃헙 권한 인증후 받아온 깃헙 토큰을 가져옴
        OAuth2AuthenticationToken oauth2 =
                (authentication instanceof OAuth2AuthenticationToken)
                    ? (OAuth2AuthenticationToken) authentication : null;

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
                            pending.events(), secret, insecure);
                } else {
                    hook = githubWebhookClient.createOrgWebhook(
                            ghAccessToken, pending.owner(), pending.events(), secret, insecure
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
                            oauth2.getAuthorizedClientRegistrationId(),principalName);
                } catch (Throwable ignore) {}
                if(session != null) session.removeAttribute(GithubHookSessionKeys.PENDING_HOOK);
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

        String targetUrl = UriComponentsBuilder.fromUriString("http://localhost:8080/")
                .queryParam("accessToken", accessToken)
                .queryParam("refreshToken", refreshToken)
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }*/
}
