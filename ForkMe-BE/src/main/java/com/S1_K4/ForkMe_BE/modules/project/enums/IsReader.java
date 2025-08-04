package com.S1_K4.ForkMe_BE.modules.project.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.project.enums
 * @fileName : IsReader
 * @date : 2025-08-04
 * @description : 팀장/팀원 enum
 */
@AllArgsConstructor
@Getter
public enum IsReader {
    READER("팀장"),
    MEMBER("팀원");

    private final String description;
}
