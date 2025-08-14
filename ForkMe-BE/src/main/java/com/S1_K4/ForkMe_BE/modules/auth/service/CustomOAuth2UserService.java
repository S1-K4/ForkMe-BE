package com.S1_K4.ForkMe_BE.modules.auth.service;

import com.S1_K4.ForkMe_BE.modules.auth.dto.CustomUserDetails;
import com.S1_K4.ForkMe_BE.modules.user.entity.User;
import com.S1_K4.ForkMe_BE.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author : 김종국
 * @packageName : com.S1_K4.ForkMe_BE.modules.auth.service
 * @fileName : CustomOAuth2UserService
 * @date : 2025-08-04
 * @description : OAuth2 로그인 진행하는 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        Map<String, Object> attributes = oAuth2User.getAttributes();

        String regId = userRequest.getClientRegistration().getRegistrationId();
        if ("github-hooks".equals(regId)) {
            // 웹훅 step-up: 토큰만 필요 → DB 저장/이메일 조회 생략
            return new DefaultOAuth2User(
                    Set.of(new SimpleGrantedAuthority("ROLE_USER")),
                    attributes,
                    "id"  // GitHub userinfo의 식별 키
            );
        }

        // git에서 받은 사용자 정보 추출
        Long gitId = ((Number) attributes.get("id")).longValue();
        String nickname = (String) attributes.get("login");
        String email = (String) attributes.get("email");
        String profileUrl = (String) attributes.get("avatar_url");

        log.info("gitId : " + gitId + " / nickname : " + nickname + " / email : " + email + " / profileUrl : " + profileUrl);

        if (email == null) {
            email = getEmailFromApi(userRequest);
            log.info("email from api = " + email);
        }
        final String emailFinal = email;

        User user = userRepository.findByGitId(gitId)
                .map(entity -> {
                    entity.updateUser(emailFinal, nickname, profileUrl);
                    entity.restore();
                    return entity;
                })
                .orElseGet(() -> {
                    return new User(gitId, emailFinal, nickname, profileUrl);
                });
        log.info("user = " + user.toString());

        userRepository.save(user);

        return new CustomUserDetails(user, attributes);
    }


    private String getEmailFromApi(OAuth2UserRequest userRequest) {
        log.info("get email from api =================");
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + userRequest.getAccessToken().getTokenValue());

        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                "https://api.github.com/user/emails",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        log.info("response = " + response.getBody());
        if (response.getBody() != null) {
            return response.getBody().stream()
                    .filter(emailMap -> (Boolean) emailMap.get("primary"))
                    .findFirst()
                    .map(emailMap -> (String) emailMap.get("email"))
                    .orElse(null);
        }

        return null;
    }


}