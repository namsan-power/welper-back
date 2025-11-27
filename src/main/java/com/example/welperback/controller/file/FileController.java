package com.example.welperback.controller.file;

import com.example.welperback.dto.file.response.VoiceUploadResponse;
import com.example.welperback.service.file.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping("/voice")
    public VoiceUploadResponse uploadVoice(@RequestParam("file") MultipartFile file) throws Exception {
        return fileService.uploadVoice(file);
    }
}
