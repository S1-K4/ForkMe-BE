package com.S1_K4.ForkMe_BE.global.common.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author : 김남이
 * @packageName : com.S1_K4.ForkMe_BE.global.common.entity
 * @fileName : Yn
 * @date : 2025-08-07
 * @description : Yes & No 를 지정할 수 있는 enum 클래스 입니다.
 */
@Getter
@RequiredArgsConstructor
public enum Yn {
    Y("Y"), //삭제 예정
    N("N"); //유지
    private final String code;
}
