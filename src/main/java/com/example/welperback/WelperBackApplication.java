package com.example.welperback;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

//비동기 기능 사용(AI <-> 서버)
@EnableAsync
@SpringBootApplication
public class WelperBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(WelperBackApplication.class, args);
    }

}
