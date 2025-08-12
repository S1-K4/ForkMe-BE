package com.S1_K4.ForkMe_BE.modules.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : 김종국
 * @packageName : com.S1_K4.ForkMe_BE.modules.user.dto
 * @fileName : UserProfile
 * @date : 2025-08-12
 * @description : 유저 정보
 */
@Getter
@AllArgsConstructor
public class UserProfile {
    private Long userPk;
    private String email;
    private String nickname;
    private String profileUrl;
}