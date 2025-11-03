package com.example.welperback.controller.test;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("/")
    public String index() {
        return "Welper 서버는 정삭 작동 중입니다 ✅ ";
    }


}
