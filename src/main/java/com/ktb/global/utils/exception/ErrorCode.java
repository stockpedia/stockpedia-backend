package com.ktb.global.utils.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    /**
     * ErrorCode 네이밍 패턴
     *
     * 패턴 A: {도메인}_{상태}
     * 무엇이 어떤 상태인가
     *
     * 패턴 B: {상황}_{대상} (중복 검증류)
     * 상황(중복/유효하지 않음/만료)이 앞, 대상이 뒤
     *
     * 패턴 C: {도메인}_{행위}_{상태} (권한 금지류)
     * 어느 도메인의 어떤 행위가 어떻게 됐는가
     *
     * 패턴 D: 공통/시스템류 (도메인 없음)
     * 특정 도메인에 속하지 않는 공통 에러, HTTP 표준 코드명을 따라가는 경우가 많음
     */
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, HttpStatus.CONFLICT.value(), "회원가입을 완료할 수 없습니다."),

    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, HttpStatus.CONFLICT.value(), "이미 사용중인 닉네임입니다."),

    MISMATCH_PASSWORD(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.value(), "일치하지 않은 비밀번호입니다."),

    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, HttpStatus.UNAUTHORIZED.value(), "이메일 또는 비밀번호가 일치하지 않습니다."),

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.value(), "존재하지 않는 회원입니다."),

    USER_UPDATE_ACCESS_FORBIDDEN(HttpStatus.FORBIDDEN, HttpStatus.FORBIDDEN.value(), "회원 수정 권한이 없습니다."),

    USER_DELETE_ACCESS_FORBIDDEN(HttpStatus.FORBIDDEN, HttpStatus.FORBIDDEN.value(), "회원 삭제 권한이 없습니다."),

    USER_UPDATE_EMPTY(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.value(), "수정할 정보가 없습니다."),



    POST_NOT_FOUND(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.value(), "존재하지 않는 게시글입니다."),

    POST_UPDATE_ACCESS_FORBIDDEN(HttpStatus.FORBIDDEN, HttpStatus.FORBIDDEN.value(), "게시글 수정 권한이 없습니다."),

    POST_DELETE_ACCESS_FORBIDDEN(HttpStatus.FORBIDDEN, HttpStatus.FORBIDDEN.value(), "게시글 삭제 권한이 없습니다."),

    POST_UPDATE_EMPTY(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.value(), "수정할 정보가 없습니다."),


    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.value(), "존재하지 않는 댓글입니다"),

    COMMENT_UPDATE_ACCESS_FORBIDDEN(HttpStatus.FORBIDDEN, HttpStatus.FORBIDDEN.value(), "댓글 수정 권한이 없습니다."),

    COMMENT_DELETE_ACCESS_FORBIDDEN(HttpStatus.FORBIDDEN, HttpStatus.FORBIDDEN.value(), "댓글 삭제 권한이 없습니다."),


    ALREADY_LIKED(HttpStatus.CONFLICT, HttpStatus.CONFLICT.value(), "이미 좋아요를 누른 게시글입니다."),

    NOT_LIKED_YET(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.value(), "좋아요를 누르지 않은 게시글입니다."),


    FILE_REQUIRED(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.value(), "파일을 첨부해주세요."),

    INVALID_FILE_NAME(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.value(), "유효하지 않은 파일명입니다."),

    INVALID_PAGE_SIZE(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.value(), "페이지 크기가 올바르지 않습니다");


    private final HttpStatus status;

    private final int code;

    private final String message;

    ErrorCode(HttpStatus status, int code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
