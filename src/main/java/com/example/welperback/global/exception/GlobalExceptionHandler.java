package com.example.welperback.global.exception;

import com.example.welperback.global.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<Object>> handleCustomException(CustomException e) {
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.error(
                        errorCode.getCode(),
                        errorCode.getMessage()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneralException(Exception e, HttpServletRequest request) throws Exception {
        String uri = request.getRequestURI();

        // Swagger, API Docs 요청은 전역 예외처리 대상에서 제외
        if (uri.startsWith("/v3/api-docs") ||
                uri.startsWith("/swagger-ui") ||
                uri.startsWith("/swagger")) {
            throw e;
        }

        e.printStackTrace();
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(
                        "SERVER001",
                        "서버 내부 오류가 발생했습니다.",
                        e.getMessage()
                ));
    }
}
