package com.S1_K4.ForkMe_BE.global.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.global.exception
 * @fileName : GlobalExceptionHandler
 * @date : 2025-08-03
 * @description : 전역 예외 처리기 입니다.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 수동 예외 (CustomException)
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<Void>> handleCustomException(CustomException ex) {
        return ResponseEntity
                .status(ex.getErrorCode().getCode())
                .body(ApiResponse.error(
                        ex.getErrorCode().getCode(),
                        ex.getErrorCode().getMessage()
                ));
    }

    /*
    * @Valid 유효성 실패 (필드 오류 리스트 포함)
    */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleValidationException(MethodArgumentNotValidException ex) {
        List<Map<String, String>> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> {
                    Map<String, String> err = new HashMap<>();
                    err.put("field", error.getField());
                    err.put("reason", error.getDefaultMessage());
                    return err;
                })
                .collect(Collectors.toList());

        Map<String, Object> data = new HashMap<>();
        data.put("errors", errors);

        return ResponseEntity
                .badRequest()
                .body(ApiResponse.error(400, "유효성 검사 실패", data));
    }

    /*
     * 예상치 못한 서버 에러
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGlobalException(Exception ex) {
        return ResponseEntity
                .internalServerError()
                .body(ApiResponse.error(500, "서버 내부 오류가 발생했습니다."));
    }
}