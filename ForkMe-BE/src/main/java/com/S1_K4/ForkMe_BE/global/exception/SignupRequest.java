package com.S1_K4.ForkMe_BE.global.exception;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.global.exception
 * @fileName : SignupRequest
 * @date : 2025-08-03
 * @description : 예외처리 테스트용 DTO
 */
@Getter
@Setter
public class SignupRequest {

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, message = "8자 이상이어야 합니다.")
    private String password;
}