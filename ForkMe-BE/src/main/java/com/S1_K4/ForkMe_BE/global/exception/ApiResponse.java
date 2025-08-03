package com.S1_K4.ForkMe_BE.global.exception;

import lombok.Builder;
import lombok.Getter;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.global.exception
 * @fileName : ApiResponse
 * @date : 2025-08-03
 * @description : 공통 응답 DTO 클래스 입니다.
 */
@Getter
@Builder
public class ApiResponse<T> {
    private final boolean success;  //요청 성공 여부
    private final int code;         //HTTP 상태 코드(ex.200, 400...) 또는 자체 에러 코드
    private final String message;   // 응답 메시지 (성공/에러 설명)
    private final T data;           // 실제 응답 데이터(없을 수도 있음)


    /*
     * 기본 성공 응답(data만 받음) : 200 상태 코드 고정, 기본 메시지 "요청 성공"
     */
    public static <T> ApiResponse<T> success(T data) {
        return success(data, 200, "요청 성공");
    }

    /*
     * 성공 응답 : 200 상태 코드 고정, 메시지 커스텀
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return success(data, 200, message);
    }

    /*
     * 성공 응답 : code,메시지 둘다 커스텀
     */
    public static <T> ApiResponse<T> success(T data, int code, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .code(code)
                .message(message)
                .data(data)
                .build();
    }


    /*
     * 실패 응답 (data : null)
     */
    public static <T> ApiResponse<T> error(int code, String message) {
        return error(code, message, null);
    }

    /*
     * 실패 응답 (data 포함)
     */
    public static <T> ApiResponse<T> error(int code, String message, T data) {
        return ApiResponse.<T>builder()
                .success(false)
                .code(code)
                .message(message)
                .data(data)
                .build();
    }
}