package com.S1_K4.ForkMe_BE.modules.project.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.project.entity
 * @fileName : ProjectStatus
 * @date : 2025-08-04
 * @description : 프로젝트 상태 enum 파일
 */
@AllArgsConstructor
@Getter
public enum ProjectStatus {
    PLANNING("기획"),
    RECRUITING("모집"),
    IN_PROGRESS("진행중"),
    ADDING("충원"),
    COMPLETED("종료");

    private final String description;
}