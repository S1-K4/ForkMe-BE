package com.S1_K4.ForkMe_BE.modules.project.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 팀장 여부 enum
 *
 * @author : 선순주
 * @fileName : IsLeader
 * @since : 2025-08-04
 */
@AllArgsConstructor
@Getter
public enum IsLeader {
    LEADER("팀장"),
    MEMBER("팀원");

    private final String description;
}
