package com.S1_K4.ForkMe_BE.modules.like.entity;

import com.S1_K4.ForkMe_BE.modules.project.entity.ProjectPosition;
import com.S1_K4.ForkMe_BE.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.like.entity
 * @fileName : Like
 * @date : 2025-08-04
 * @description : 좋아요 엔티티
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
@Builder
@AllArgsConstructor
@Table(name="like")
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="like_pk")
    private Long likePk;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_pk", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="project_position_pk", nullable = false)
    private ProjectPosition projectPosition;
}