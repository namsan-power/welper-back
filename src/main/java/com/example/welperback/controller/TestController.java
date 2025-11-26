package com.example.welperback.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("/")
    public String index() {
        return "✅Welper서버가 작동 중입니다";
    }
}

