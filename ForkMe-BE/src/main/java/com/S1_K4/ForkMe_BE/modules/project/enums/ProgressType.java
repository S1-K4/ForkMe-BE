package com.S1_K4.ForkMe_BE.modules.project.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.project.enums
 * @fileName : ProgressType
 * @date : 2025-08-04
 * @description : 프로젝트 진행방식 enum
 */
@AllArgsConstructor
@Getter
public enum ProgressType {
    ONLINE("온라인"),
    OFFLINE("오프라인"),
    HYBRID("온/오프라인");

    private final String description;

}
