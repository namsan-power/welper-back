package com.example.welperback.global.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    // Authentication
    INVALID_CREDENTIALS("AUTH001", HttpStatus.UNAUTHORIZED, "잘못된 인증 정보입니다."),
    USER_NOT_FOUND("AUTH002", HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."),
    DUPLICATE_USER_ID("AUTH003", HttpStatus.CONFLICT, "이미 사용 중인 사용자 ID입니다."),
    INVALID_TOKEN("AUTH004", HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    TOKEN_EXPIRED("AUTH005", HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),
    
    // Authorization
    FORBIDDEN("AUTH006", HttpStatus.FORBIDDEN, "해당 요청에 대한 권한이 없습니다."),
    ADMIN_NOT_FOUND("AUTH007", HttpStatus.NOT_FOUND, "존재하지 않는 관리자입니다."),
    
    // Client
    CLIENT_NOT_FOUND("CLIENT001", HttpStatus.NOT_FOUND, "존재하지 않는 클라이언트입니다."),
    
    // Validation
    INVALID_REQUEST("COMMON001", HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    
    // File
    FILE_UPLOAD_FAILED("FILE001", HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다."),
    
    // Server
    INTERNAL_SERVER_ERROR("SERVER001", HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다.");

    private final String code;
    private final HttpStatus status;
    private final String message;

    ErrorCode(String code, HttpStatus status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
