package com.S1_K4.ForkMe_BE.global.config.springsecurity;

import com.S1_K4.ForkMe_BE.global.security.jwt.JwtTokenFilter;
import com.S1_K4.ForkMe_BE.modules.auth.OAuth2AuthenticationSuccessHandler;
import com.S1_K4.ForkMe_BE.modules.auth.service.CustomOAuth2UserService;
import com.S1_K4.ForkMe_BE.modules.on_project.webhook.GithubHookSessionKeys;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.concurrent.TimeUnit;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.global.config.springsecurity
 * @fileName : SecurityConfig
 * @date : 2025-08-03
 * @description : springSecurity 관련 설정파일입니다.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    @Value("${spring.security.oauth2.client.registration.github-hooks.redirect-uri}")
    private String fixedRedirectUri;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final JwtTokenFilter jwtTokenFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,OAuth2AuthorizationRequestResolver customResolver) throws Exception {
        http.csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )
                .authorizeHttpRequests(auth -> auth

                        // ====== Public ======
                        .requestMatchers(
                                "/", "/index.html", "/login/**", "/api/auth/**",
                                "/favicon.ico", "login-success.html", "/login-error"
                        ).permitAll()

                        // ====== Swagger 관련 경로 ======
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()

                        // ====== Projects : 인증 필요 ======
                        .requestMatchers(HttpMethod.GET,
                                "/api/projects/form-info",          // 생성폼 조회
                                "/api/projects/*/update-form")      // 수정폼 조회
                        .authenticated()

                        // ====== Projects : 인증X ======
                        .requestMatchers(HttpMethod.GET,
                                "/api/projects",                // 프로젝트 목록 조회
                                "/api/projects/*",               // 프로젝트 상세 조회
                                "/api/projects/*/members"       //프로젝트 멤버 조회
                        ).permitAll()

                        // ====== Projects : 인증 필요 ======
                        .requestMatchers(HttpMethod.POST, "/api/projects").authenticated()          // 프로젝트 생성
                        .requestMatchers(HttpMethod.PUT, "/api/projects/*").authenticated()         // 프로젝트 수정
                        .requestMatchers(HttpMethod.DELETE, "/api/projects/*").authenticated()      // 프로젝트 삭제
                        .requestMatchers(HttpMethod.POST,
                                "/api/projects/*/status/recruiting", // 상태: 기획 -> 모집
                                "/api/projects/*/status/progress",   // 상태: 모집 -> 진행
                                "/api/projects/*/status/adding",     // 상태: 진행 -> 충원
                                "/api/projects/*/status/complete"    // 상태: 진행 -> 종료
                        ).authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/projects/*/title").authenticated()        // 프로젝트 명 변경
                        .requestMatchers(HttpMethod.DELETE,
                                "/api/projects/*/members/me",       //(팀원) 탈퇴
                                "/api/projects/*/members/*"         //(팀장) 강퇴
                                ).authenticated()


                        // ====== Applies(신청서) GET 매핑 : 인증 필요 ======
                        .requestMatchers(HttpMethod.GET,
                                "/api/projects/*/applies/form", // 생성폼 조회
                                "/api/projects/*/applies",      // (팀장) 목록 조회
                                "/api/projects/*/applies/*"     // 단건 조회
                        ).authenticated()

                        // ====== Applies(신청서) POST 매핑 : 인증 필요 ======
                        .requestMatchers(HttpMethod.POST,
                                "/api/projects/*/applies",           // 신청서 작성
                                "/api/projects/*/applies/*/cancel",  // 신청서 취소 (status 변경)
                                "/api/projects/*/applies/*/approve", // (팀장) 신청서 수락
                                "/api/projects/*/applies/*/reject"   // (팀장) 신청서 거절
                        ).authenticated()

                        // ====== Likes (좋아요) : 인증X ======
                        .requestMatchers(HttpMethod.GET, "/api/likes/*").permitAll() // 좋아요 수 카운트

                        // ====== Likes (좋아요) : 인증 필요 ======
                        .requestMatchers(HttpMethod.GET, "/api/likes/*/me").authenticated()    // 좋아요 여부 조회
                        .requestMatchers(HttpMethod.POST, "/api/likes/*").authenticated()       // 좋아요 추가
                        .requestMatchers(HttpMethod.DELETE, "/api/likes/*").authenticated()     // 좋아요 삭제

                        // ====== Comments (댓글) : 인증 필요 ======
                        .requestMatchers(HttpMethod.POST, "/api/comments/*").authenticated()    // 댓글 등록, 수정
                        .requestMatchers(HttpMethod.DELETE, "/api/comments/*").authenticated()  // 댓글 삭제

                        // ====== Preflight ======
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        .requestMatchers(
                                "/api/on-project/**", "boardIn/**",
                                "/api/schedules/**", "/api/on-project/comments/**"
                        ).permitAll() // 0812 김송이 추가

                        // ====== 채팅 : 인증X ======
                        .requestMatchers(HttpMethod.GET, "/api/chatting-room/*/participants").permitAll() // 채팅 참여자 리스트 조회

                        // ====== 채팅 : 인증 필요 ======
                        .requestMatchers(HttpMethod.GET,
                                "/api/chatting-room/create", //채팅방 생성
                                "/api/chatting-room/private", //로그인 유저의 프로젝트 내 개인채팅방 리스트 조회
                                "/api/chatting-room/*/messages/**" //로그인한 유저 기준 채팅방 이전 대화 조회
                                ).authenticated()

                        // ====== 알람 : 인증 필요 ======
                        .requestMatchers(HttpMethod.GET,
                                "/api/alarm/**" //알람 리스트 가져오기
                        ).authenticated()
                        // ====== 알람 : 인증 필요 ======
                        .requestMatchers(HttpMethod.DELETE,
                                "/api/alarm/**" //알람 삭제하기
                        ).authenticated()


                        // 인증 필요
                        .requestMatchers(HttpMethod.GET, "/api/user/me/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/user/me/**").authenticated()

                        // 인증X
                        .requestMatchers(HttpMethod.GET, "/api/mypage/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/github/webhooks").permitAll()
                        .requestMatchers("/hook-test.html", "/hook-result.html").permitAll()
                        .requestMatchers("/oauth2/authorization/**", "/login/oauth2/code/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/github/**").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/oauth2/authorization/**").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/api/**").permitAll()

                        .anyRequest().authenticated()
                )
                .exceptionHandling(e -> e
                        // 인증이 안된 사용자가 접근할 때 (401)
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write(
                                    "{\"success\":false,\"code\":401,\"message\":\"인증이 필요한 요청입니다.\"}"
                            );
                        })
                        // 인증은 되었지만 권한이 없을 때 (403)
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write(
                                    "{\"success\":false,\"code\":403,\"message\":\"접근 권한이 없습니다.\"}"
                            );
                        })
                )
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(auth -> auth.authorizationRequestResolver(customResolver).baseUri("/oauth2/authorization"))
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                )
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public OAuth2AuthorizationRequestResolver customAuthorizationRequestResolver(
            ClientRegistrationRepository clientRegistrationRepository) {

        DefaultOAuth2AuthorizationRequestResolver defaultResolver =
                new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository, "/oauth2/authorization");

        return new OAuth2AuthorizationRequestResolver() {
            @Override
            public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
                OAuth2AuthorizationRequest req = defaultResolver.resolve(request);
                // URI에서 registrationId 추출
                String registrationId = extractRegistrationIdFromRequest(request);
                return customizeAndStore(req, request, registrationId);
            }

            @Override
            public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
                OAuth2AuthorizationRequest req = defaultResolver.resolve(request, clientRegistrationId);
                return customizeAndStore(req, request, clientRegistrationId);
            }

            private OAuth2AuthorizationRequest customizeAndStore(OAuth2AuthorizationRequest req, HttpServletRequest request, String registrationId) {
                if (req == null) return null;

                // redirect_uri 고정
                OAuth2AuthorizationRequest swapped = OAuth2AuthorizationRequest.from(req)
                        .redirectUri(fixedRedirectUri)
                        .build();

                // 오직 github-hooks 등록에 대해서만 session -> redis 저장
                if ("github-hooks".equals(registrationId)) {
                    HttpSession session = request.getSession(false);
                    if (session != null) {
                        Object pendingObj = session.getAttribute(GithubHookSessionKeys.PENDING_HOOK);
                        if (pendingObj != null) {
                            try {
                                String key = "pending_hook:" + swapped.getState();
                                String json = objectMapper.writeValueAsString(pendingObj);
                                redisTemplate.opsForValue().set(key, json, 10, TimeUnit.MINUTES);
                                log.info("customResolver: saved pending to redis key={}", key);
                                session.removeAttribute(GithubHookSessionKeys.PENDING_HOOK);
                            } catch (Exception e) {
                                log.error("customResolver: failed to save pending to redis", e);
                            }
                        }
                    }
                }

                return swapped;
            }

            private String extractRegistrationIdFromRequest(HttpServletRequest request) {
                String uri = request.getRequestURI();
                if (uri == null) return null;
                String prefix = "/oauth2/authorization/";
                int idx = uri.indexOf(prefix);
                if (idx < 0) return null;
                String registrationId = uri.substring(idx + prefix.length());
                int q = registrationId.indexOf('?');
                if (q >= 0) registrationId = registrationId.substring(0, q);
                return registrationId;
            }
        };
    }
}
