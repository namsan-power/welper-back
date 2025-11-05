package com.example.welperback.global.exception;

import com.example.welperback.global.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<Object>> handleCustomException(CustomException e) {
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.error(errorCode.getMessage(), errorCode.getStatus().value()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneralException(Exception e, HttpServletRequest request) {
        String uri = request.getRequestURI();

        // ✅ Swagger 요청은 예외 처리에서 제외
        if (uri.startsWith("/v3/api-docs") || uri.startsWith("/swagger")) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        e.printStackTrace();
        return ResponseEntity
                .status(500)
                .body(ApiResponse.error("서버 내부 오류가 발생했습니다.", 500));
    }
}
