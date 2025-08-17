package com.S1_K4.ForkMe_BE.global.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
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

    // 400 - JSON 파싱 실패
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Object>> handleJsonParse(HttpMessageNotReadableException ex) {
        return ResponseEntity.status(400).body(ApiResponse.error(400, "요청 형식이 잘못되었습니다."));
    }

    // 403 - 권한 없음
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(403).body(ApiResponse.error(403, "접근 권한이 없습니다."));
    }

    // 415 - Content-Type 오류
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiResponse<Object>> handleMediaType(HttpMediaTypeNotSupportedException ex) {
        return ResponseEntity.status(415).body(ApiResponse.error(415, "지원하지 않는 Content-Type입니다."));
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleMediaType2(IllegalArgumentException ex) {
        return ResponseEntity.status(400).body(ApiResponse.error(415, "@PathVariable 누락"));
    }


    // 500 - 그 외 모든 예외
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleOther(Exception ex) {
        return ResponseEntity.status(500).body(ApiResponse.error(500, ex.getMessage()));
    }


}