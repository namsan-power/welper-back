package com.example.welperback.dto.file.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VoiceUploadResponse {
    private String filePath;
    private String fileName;
}
