package com.example.welperback.global.exception;

import com.example.welperback.global.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import lombok.extern.slf4j.Slf4j;


import java.util.Map;
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * ✔ CustomException 처리
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<Object>> handleCustomException(CustomException e) {

        log.error("❌ CustomException 발생");
        log.error("ErrorCode: {}", e.getErrorCode());
        log.error("Message: {}", e.getMessage(), e);
        ErrorCode errorCode = e.getErrorCode();

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.error(
                        errorCode.getMessage(),
                        errorCode.getStatus().value(),
                        errorCode.getDetails()
                ));
    }

    /**
     * ✔ 모든 예기치 못한 오류 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneralException(
            Exception e,
            HttpServletRequest request
    ) {

        String uri = request.getRequestURI();

        // Swagger 요청은 제외
        if (uri.startsWith("/v3/api-docs") ||
                uri.startsWith("/swagger") ||
                uri.startsWith("/swagger-ui")) {
            throw new RuntimeException(e);
        }

        e.printStackTrace();

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(
                        "서버 내부 오류가 발생했습니다.",
                        500,
                        Map.of(
                                "type", e.getClass().getSimpleName(),
                                "message", e.getMessage()
                        )
                ));
    }
}
