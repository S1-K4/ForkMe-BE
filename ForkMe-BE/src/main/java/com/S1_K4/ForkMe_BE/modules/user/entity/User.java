package com.S1_K4.ForkMe_BE.modules.user.entity;

import com.S1_K4.ForkMe_BE.global.common.entity.BaseTime;
import jakarta.persistence.*;
import lombok.*;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.user.entity
 * @fileName : User
 * @date : 2025-08-04
 * @description : 유저 엔티티
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Getter
@Table(name="user")
@Builder
public class User extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_pk")
    private Long userPk;

    @Column(name="email")
    private String email;

    @Column(name="nickname")
    private String nickname;

    @Column(name="profile_url")
    private String profileUrl;

}