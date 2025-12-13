package com.example.welperback.dto.file;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class DocumentUploadResponse {
    private String id;
    private String fileName;
    private String contentType;
    private Long size;
    private String stage;
    private String category;
}

