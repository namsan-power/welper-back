package com.example.welperback.service.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    /**
     * 파일을 로컬 폴더에 저장하고 고유한 파일 이름 반환.
     * @param voiceFile
     * @return String
     */
    public String upload(MultipartFile voiceFile) {
        
        String randomId = UUID.randomUUID().toString(); 
        
        //원본 파일 이름에서 확장자(.mp3)만 떼어낸다.
        String originalName = voiceFile.getOriginalFilename();
        String extension = "";
        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf("."));
        }

        // 새로운 고유 파일 이름을 만든다. (예: 랜덤ID.mp3)
        String uniqueFileName = randomId + extension;

        // 저장 경로(uploadDir)와 고유 파일 이름을 합쳐서 최종 파일 경로를 만든다.
        // (Paths.get() : C드라이브 경로와 파일 이름을 안전하게 합쳐줌)
        Path filePath = Paths.get(uploadDir, uniqueFileName);

        try {
            // 파일을 최종 파일 경로에 저장(복사)한다!
            Files.copy(voiceFile.getInputStream(), filePath);

        } catch (IOException e) {
            // 저장 실패 (예: 폴더가 없거나, 디스크가 꽉 찼거나)
            System.err.println("파일 저장 실패: " + e.getMessage());
            throw new RuntimeException("파일을 로컬에 저장하는 데 실패했습니다.", e);
        }

        System.out.println("--- [FileService 로그] ---");
        System.out.println("파일 저장 성공: " + filePath.toString());
        
        // DB에 저장할 고유한 파일 이름만 반환한다.
        return uniqueFileName;
    }
}