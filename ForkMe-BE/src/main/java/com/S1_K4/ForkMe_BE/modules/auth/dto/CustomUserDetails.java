package com.S1_K4.ForkMe_BE.modules.auth.dto;

import com.S1_K4.ForkMe_BE.modules.user.entity.User;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * @author : 김종국
 * @packageName : com.S1_K4.ForkMe_BE.modules.auth.dto
 * @fileName : CustomUserDetails
 * @date : 2025-08-04
 * @description : spring security와 user 엔티티 담는 클래스
 */

@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails, OAuth2User {

    private final User user;
    private final Map<String, Object> attributes;

    public User getUser() {
        return user;
    }

    @Override
    public String getUsername() {
        return user.getNickname();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return user.getEmail();
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 모든 로그인한 사용자에게 "ROLE_USER" 권한을 부여
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}