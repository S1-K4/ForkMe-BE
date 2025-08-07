package com.S1_K4.ForkMe_BE.global.common.common_enum;

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
    Y("Yes 값"), //delete_yn 에서 사용 시 -> 삭제된 데이터
    N("No 값"); //delete_yn 에서 사용 시 -> 서비스에 존재하는 데이터
    private final String code;
}
