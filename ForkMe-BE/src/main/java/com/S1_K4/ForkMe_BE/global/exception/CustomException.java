package com.S1_K4.ForkMe_BE.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.global.exception
 * @fileName : CustomException
 * @date : 2025-08-03
 * @description : 수동 검증 예외 + 커스텀 에러 코드 관련 클래스입니다.
 */
@Getter
public class CustomException extends RuntimeException {

    private final ErrorCode errorCode;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    /*
    * ENUM 타입 에러코드 설정
    */
    @Getter
    @AllArgsConstructor
    public enum ErrorCode {
        BAD_REQUEST(400, "잘못된 요청입니다."),
        INVALID_PASSWORD(400, "비밀번호가 너무 짧습니다."),
        INTERNAL_SERVER_ERROR(500, "서버 오류입니다."),
        UNAUTHORIZED_REQUEST(401, "권한이 없습니다."),
        UNAUTHORIZED(401, "인증이 필요합니다.");

        private final int code;
        private final String message;
    }
}