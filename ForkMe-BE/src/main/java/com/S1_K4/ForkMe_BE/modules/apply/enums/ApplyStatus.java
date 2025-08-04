package com.S1_K4.ForkMe_BE.modules.apply.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.apply.enums
 * @fileName : ApplyStatus
 * @date : 2025-08-04
 * @description : 신청서 상태 enum
 */
@AllArgsConstructor
@Getter
public enum ApplyStatus {
    PENDING("대기"),
    APPROVED("수락"),
    REJECTED("거절");

    private final String description;
}
