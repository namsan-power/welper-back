package com.example.welperback.dto.file;

import com.example.welperback.domain.file.DocumentFile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class DocumentResponse {
    private String id;
    private String caseNumber;
    private String fileName;
    private String contentType;
    private Long size;
    private String stage;
    private String category;
    private String status;
    private String uploaderId;
    private LocalDateTime createdAt;

    public static DocumentResponse from(DocumentFile file) {
        return DocumentResponse.builder()
                .id(file.getId())
                .caseNumber(file.getClient().getCaseNumber())
                .fileName(file.getFileName())
                .contentType(file.getContentType())
                .size(file.getSize())
                .stage(file.getStage())
                .category(file.getCategory())
                .status(file.getStatus())
                .uploaderId(file.getUploader() != null ? file.getUploader().getUserId() : null)
                .createdAt(file.getCreatedAt())
                .build();
    }
}

