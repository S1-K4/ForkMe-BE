package com.S1_K4.ForkMe_BE.modules.user.entity;

import com.S1_K4.ForkMe_BE.global.common.entity.BaseTime;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.user.entity
 * @fileName : User
 * @date : 2025-08-04
 * @description : 유저 엔티티
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name="user")
@Builder
public class User extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_pk")
    private Long userPk;

    @Column(name = "git_id")
    private Long gitId;

    @Column(name="email")
    private String email;

    @Column(name="nickname")
    private String nickname;

    @Column(name="profile_url")
    private String profileUrl;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserTechStack> userTechStacks = new ArrayList<>();

    public User(Long gitId, String email, String nickname, String profileUrl) {
        this.gitId = gitId;
        this.email = email;
        this.nickname = nickname;
        this.profileUrl = profileUrl;
    }

    public void updateUser(String email, String nickname, String profileUrl) {
        this.email = email;
        this.nickname = nickname;
        this.profileUrl = profileUrl;
    }

    public void withdraw(){
        this.markDeleted(); // deleted_yn = Y

        // 개인정보 변경
        this.nickname = "탈퇴한 사용자";
        this.email = "deleted@deleted.deleted";
        this.profileUrl = "https://avatars.githubusercontent.com/u/99146600?v=4";

    }
}