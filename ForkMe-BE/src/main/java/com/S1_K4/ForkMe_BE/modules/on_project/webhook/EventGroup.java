package com.S1_K4.ForkMe_BE.modules.on_project.webhook;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : 김관중
 * @packageName : com.S1_K4.ForkMe_BE.modules.on_project.webhook
 * @fileName : eventGroup
 * @date : 2025-08-07
 * @description : 웹훅 이벤트 종류 이넘 클래스입니다.
 */
@AllArgsConstructor
@Getter
public enum EventGroup {
    PUSH("push"),
    PULL_REQUEST("pull_request"),
    ISSUES("issues"),
    ISSUE_COMMENT("issue_comment"),
    RELEASE("release"),
    CREATE_DELETE("create_delete"),
    OTHERS("others");

    private final String description;
}
