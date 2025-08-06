package com.S1_K4.ForkMe_BE.modules.auth.entity;

import com.S1_K4.ForkMe_BE.global.common.entity.BaseTime;
import com.S1_K4.ForkMe_BE.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * @author : 김종국
 * @packageName : com.S1_K4.ForkMe_BE.modules.auth.entity
 * @fileName : Auth
 * @date : 2025-08-04
 * @description : auth 테이블 엔티티입니다
 */

@Getter
@Setter
@Entity
@Table(name = "auth")
public class Auth extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long authPk;

    @Column(name = "refresh_token", nullable = false)
    private String refreshToken;

    @Column(name = "access_token", nullable = false)
    private String accessToken;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_pk", nullable = false)
    private User user;

    public Auth(User user) {
        this.user = user;
    }

    public void updateAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

}