package com.example.welperback.controller;

import com.example.welperback.global.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/")
    public ApiResponse<String> test() {
        return ApiResponse.success("Welper API Server is running!");
    }

    @GetMapping("/health")
    public ApiResponse<String> health() {
        return ApiResponse.success("OK");
    }
}
