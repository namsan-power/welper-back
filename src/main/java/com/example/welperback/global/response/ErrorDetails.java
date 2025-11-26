package com.example.welperback.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorDetails {
    private final String code;
    private final String message;
    private final String details;
}
