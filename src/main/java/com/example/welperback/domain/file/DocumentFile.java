package com.example.welperback.domain.file;

import com.example.welperback.domain.account.User;
import com.example.welperback.domain.client.Client;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "document_file")
public class DocumentFile {

    @Id
    private String id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "case_number")
    private Client client;

    private String stage;    // INTAKE, ASSESSMENT, PLANNING, EXECUTION, RESULT ...
    private String category; // VISIT_REPORT, SERVICE_PLAN, CONTRACT, etc.

    private String fileName;    // 원본 파일명
    private String contentType; // MIME type
    private Long size;          // bytes

    private String storagePath; // 로컬/NAS 경로

    @ManyToOne
    @JoinColumn(name = "uploader_id")
    private User uploader;

    private String status;      // READY, DELETED

    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (status == null) {
            status = "READY";
        }
    }
}

