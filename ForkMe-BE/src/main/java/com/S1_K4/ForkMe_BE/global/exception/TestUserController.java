package com.S1_K4.ForkMe_BE.global.exception;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.global.exception
 * @fileName : TestUserController
 * @date : 2025-08-03
 * @description : 테스트 유저 컨트롤러 입니다.
 */
@RestController
@RequestMapping("/api/users")
public class TestUserController {

    /* 1. 기본 성공 응답
        {
            "success": true,
            "code": 200,
            "message": "요청 성공",
            "data": "기본 성공 처리됨"
        }
     */
    @GetMapping("/basic-success")
    public ResponseEntity<ApiResponse<String>> basicSuccess() {
        String result = "기본 성공 처리됨";
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /* 2. 성공 코드(200) + 커스텀 메시지
        {
            "success": true,
            "code": 200,
            "message": "닉네임 변경 성공",
            "data": "닉네임 업데이트 완료"
        }
     */
    @GetMapping("/custom-message")
    public ResponseEntity<ApiResponse<String>> customMessageSuccess() {
        String result = "닉네임 업데이트 완료";
        return ResponseEntity.ok(ApiResponse.success(result, "닉네임 변경 성공"));
    }

    /* 3. 성공 코드 설정 + 커스텀 메시지 + 커스텀 코드
        {
            "success": true,
            "code": 201,
            "message": "회원가입 완료",
            "data": "회원가입 처리됨"
        }
     */
    @GetMapping("/created")
    public ResponseEntity<ApiResponse<String>> created() {
        return ResponseEntity.status(201)
                .body(ApiResponse.success("회원가입 처리됨", 201, "회원가입 완료"));
    }

    /* 4. 성공 코드 설정 + 커스텀 메시지 + 커스텀 코드(dto 반환)
        {
          "success": true,
          "code": 200,
          "message": "회원가입 정보 응답",
          "data": {
            "email": "test@example.com",
            "password": "12345678"
          }
        }
 */
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignupRequest>> signup(@RequestBody @Valid SignupRequest request) {
        return ResponseEntity.ok(ApiResponse.success(request,200, "회원가입 정보 응답"));
    }

    /* 5. 수동 검증 실패 (비밀번호 짧음)
        --1. 성공시
        {
            "success": true,
            "code": 200,
            "message": "비밀번호 조건 통과",
            "data": null
        }

        --2. 실패시
        {
            "success": false,
            "code": 400,
            "message": "비밀번호가 너무 짧습니다.",
            "data": null
        }
     */
    @PostMapping("/manual-check")
    public ResponseEntity<ApiResponse<Void>> manualCheck(@RequestBody SignupRequest request) {
        if (request.getPassword() == null || request.getPassword().length() < 8) {
            throw new CustomException(CustomException.ErrorCode.INVALID_PASSWORD);  //커스텀 에러코드
        }
        return ResponseEntity.ok(ApiResponse.success(null, "비밀번호 조건 통과"));
    }

    /* 6. @Valid 유효성 검증
        --1. 성공시
        {
            "success": true,
            "code": 200,
            "message": "입력값 유효",
            "data": null
        }

        --2. 실패시
        {
            "success": false,
            "code": 400,
            "message": "유효성 검사 실패",
            "data": {
                "errors": [
                    {
                        "reason": "8자 이상이어야 합니다.",
                        "field": "password"
                    }
                ]
            }
        }
     */
    @PostMapping("/valid-check")
    public ResponseEntity<ApiResponse<Void>> validCheck(@RequestBody @Valid SignupRequest request) {
        // 유효성 통과 시
        return ResponseEntity.ok(ApiResponse.success(null, "입력값 유효"));
    }

    // 7. 의도적인 서버 예외 발생 테스트
    @GetMapping("/server-error")
    public ResponseEntity<ApiResponse<Void>> serverError() {
        throw new RuntimeException("테스트용 서버 에러");
    }
}