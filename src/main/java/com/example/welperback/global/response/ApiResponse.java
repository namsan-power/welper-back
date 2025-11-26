package com.example.welperback.global.response;

import lombok.Getter;

@Getter
public class ApiResponse<T> {
    private final boolean success;
    private final T data;
    private final ErrorDetails error;

    private ApiResponse(boolean success, T data, ErrorDetails error) {
        this.success = success;
        this.data = data;
        this.error = error;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null);
    }

    public static <T> ApiResponse<T> error(String code, String message, String details) {
        return new ApiResponse<>(false, null, new ErrorDetails(code, message, details));
    }

    public static <T> ApiResponse<T> error(String code, String message) {
        return new ApiResponse<>(false, null, new ErrorDetails(code, message, null));
    }
}
