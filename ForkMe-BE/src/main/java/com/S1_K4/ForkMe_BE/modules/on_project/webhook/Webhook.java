package com.S1_K4.ForkMe_BE.modules.on_project.webhook;

import com.S1_K4.ForkMe_BE.global.common.entity.BaseTime;
import com.S1_K4.ForkMe_BE.modules.project.entity.Project;
import com.S1_K4.ForkMe_BE.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

/**
 * @author : 김관중
 * @packageName : com.S1_K4.ForkMe_BE.modules.on_project.webhook
 * @fileName : Webhook
 * @date : 2025-08-07
 * @description : 깃허브 웹훅 엔티티 입니다.
 */

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "github_timeline")
public class Webhook extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "git_timeline_pk")
    private Long gitTimelinePk;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_pk", nullable = false)
    private Project project;

    @Column(name = "event_type", nullable = false, length = 50)
    private String eventType;

    @Column(name = "event_group", nullable = false, length = 20)
    private EventGroup eventGroup;

    @Column(name = "event_summary")
    private String eventSummary;

    @Column(name = "delivery_id", length = 36, unique = true)
    private String deliveryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_user_pk")
    private User actorUser;

    @Column(name = "actor_login", length = 100)
    private String actorLogin;

    @Column(name = "actor_avatar")
    private String actorAvatar;

    @Column(name = "repository_id")
    private Long repositoryId;

    @Column(name = "repository_full_name", length = 100)
    private String repositoryFullName;

    @Column(name = "organization_login", length = 100)
    private String organizationLogin;

}
