package com.example.welperback.global.response;

import lombok.Getter;

@Getter
public class ApiResponse<T> {

    private final boolean success;
    private final String message;
    private final int code;
    private final T data;
    private final Object error;

    private ApiResponse(boolean success, String message, int code, T data, Object error) {
        this.success = success;
        this.message = message;
        this.code = code;
        this.data = data;
        this.error = error;
    }
    // 응답 버전 1
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, 200, data, null);
    }
    // 응답 버전 2
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, null, 200, data, null);
    }
    public static <T> ApiResponse<T> error(String message, int code, Object errorDetails) {
        return new ApiResponse<>(false, message, code, null, errorDetails);
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public int getCode() { return code; }
    public T getData() { return data; }
    public Object getError() { return error; }
}
