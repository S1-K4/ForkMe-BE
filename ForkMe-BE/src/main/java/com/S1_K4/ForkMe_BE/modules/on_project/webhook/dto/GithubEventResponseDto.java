package com.S1_K4.ForkMe_BE.modules.on_project.webhook.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

/**
 * @author : 김관중
 * @packageName : com.S1_K4.ForkMe_BE.modules.on_project.webhook.dto
 * @fileName : GithubEventResponseDto
 * @date : 2025-08-20
 * @description : Github webhook 응답 dto 입니다
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GithubEventResponseDto {

    @JsonProperty("git_timeline_pk")
    private Long gitTimelinePk;          // git_timeline_pk (PK, auto_increment)

    @JsonProperty("project_pk")
    private Long projectPk;              // project_pk

    @JsonProperty("event_type")
    private String eventType;            // event_type

    @JsonProperty("event_group")
    private String eventGroup;           // event_group

    @JsonProperty("event_summary")
    private String eventSummary;         // event_summary (프론트의 title/summary로 사용 가능)

    @JsonProperty("delivery_id")
    private String deliveryId;           // delivery_id (char(36), unique)

    @JsonProperty("actor_user_pk")
    private Long actorUserPk;            // actor_user_pk

    @JsonProperty("actor_login")
    private String actorLogin;           // actor_login (author.name 역할)

    @JsonProperty("actor_avatar")
    private String actorAvatar;          // actor_avatar (author.avatar 역할)

    @JsonProperty("repository_id")
    private Long repositoryId;           // repository_id

    @JsonProperty("repository_full_name")
    private String repositoryFullName;   // repository_full_name (owner/repo 형태)

    @JsonProperty("organization_login")
    private String organizationLogin;    // organization_login

    /**
     * 프론트에서 Date 객체로 사용하려면 ISO 문자열로 직렬화됩니다.
     * Jackson으로 직렬화 시 "yyyy-MM-dd'T'HH:mm:ss" 형식(예: 2025-08-20T12:34:56)으로 내려가게 설정.
     */
    @JsonProperty("created_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;     // created_at (datetime)

}
