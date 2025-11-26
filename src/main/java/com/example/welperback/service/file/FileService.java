package com.example.welperback.service.file;

import com.example.welperback.dto.file.response.VoiceUploadResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;

import java.util.UUID;

@Service
public class FileService {

    private String getUploadDir() {
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("mac")) {
            return System.getProperty("user.home") + "/welper-uploads";
        } else if (os.contains("win")) {
            return System.getProperty("user.home") + "\\welper-uploads";
        } else {
            return "/home/welper/uploads";
        }
    }

    public VoiceUploadResponse uploadVoice(MultipartFile file) throws IOException {

        String baseDir = getUploadDir();
        Files.createDirectories(Paths.get(baseDir));

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path path = Paths.get(baseDir, fileName);

        Files.write(path, file.getBytes());
        System.out.println("UPLOAD DIR = " + getUploadDir());

        return VoiceUploadResponse.builder()
                .filePath(path.toString())
                .fileName(fileName)
                .build();
    }
}
