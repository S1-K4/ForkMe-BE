package com.S1_K4.ForkMe_BE.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

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
        UNAUTHORIZED(401, "인증이 필요합니다."),
        FORBIDDEN(403, "권한이 없습니다."),
        USER_NOT_FOUND(404,"찾을 수 없는 유저입니다."),

        //프로젝트 관련 에러코드
        PROJECT_NOT_FOUND(404,"프로젝트를 찾을 수 없습니다."),
        PROJECT_ALREDAY_DELETE(404, "이미 삭제된 프로젝트입니다."),
        PROJECT_STATUS_CHANGE(403,"프로젝트 상태를 변경할 수 없습니다."),
        INVALID_INPUT_VALUE(403, "입력값이 올바르지 않습니다."),
        MEMBER_NOT_FOUND(403, "멤버를 찾을 수 없습니다."),
        LEADER_CANNOT_LEAVE(403, "팀장은 프로젝트에서 탈퇴할 수 없습니다."),
        PROJECT_TITLE_CHANGE(403, "프로젝트명을 변경할 수 없습니다."),
        PROJECT_NOT_UPDATE(403, "프로젝트명을 변경할 수 없습니다."),

        //댓글 관련 에러 코드
        PARENT_COMMENT_NOT_FOUND(404, "상위 댓글을 찾을 수 없습니다."),
        COMMENT_NOT_FOUND(404, "댓글을 찾을 수 없습니다."),

        //좋아요 관련 에러코드
        ALREDAY_LIKED(500, "이미 좋아요를 누른 프로젝트입니다."),
        LIKED_NOT_FOUND(500, "좋아요 내역이 없습니다."),

        //신청서 관련 에러코드
        INVALID_PROJECT_POSITION(400, "모집분야가 일치하지 않습니다."),
        INVALID_TECH_SELECTION(400, "기술스택이 일치하지 않습니다."),
        ALREADY_APPLIED(400, "이미 신청한 프로젝트입니다."),
        APPLY_NOT_FOUND(404,"신청서를 찾을 수 없습니다."),
        LEADER_CANNOT_APPLY(403,"팀장은 신청이 불가합니다."),
        INVALID_STATUS_CHANGE(403, "이미 처리된 신청서는 취소가 불가능합니다."),
        APPLY_NOT_WRITTEN(403,"기획상태에서는 신청서 작성이 불가합니다.");

        private final int code;
        private final String message;
    }
}