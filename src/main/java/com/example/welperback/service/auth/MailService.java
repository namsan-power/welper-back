package com.example.welperback.service.auth;

import org.springframework.stereotype.Service;

@Service
public class MailService {

    public void sendResetEmail(String toEmail, String resetLink) {
        // 실제로는 JavaMailSender 사용, 지금은 임시로 콘솔 출력
        System.out.println("[메일 전송]");
        System.out.println("수신자: " + toEmail);
        System.out.println("재설정 링크: " + resetLink);
    }
}