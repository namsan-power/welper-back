package com.example.welperback.service.file;

import com.example.welperback.domain.account.User;
import com.example.welperback.domain.client.Client;
import com.example.welperback.domain.file.DocumentFile;
import com.example.welperback.dto.file.DocumentResponse;
import com.example.welperback.dto.file.DocumentUploadResponse;
import com.example.welperback.global.exception.CustomException;
import com.example.welperback.global.exception.ErrorCode;
import com.example.welperback.repository.ClientRepository;
import com.example.welperback.repository.DocumentFileRepository;
import com.example.welperback.repository.account.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings({"NullAway", "null", "ConstantConditions"})
@Transactional(readOnly = true)
public class DocumentFileService {

    private final ClientRepository clientRepository;
    private final DocumentFileRepository documentFileRepository;
    private final UserRepository userRepository;

    @Value("${storage.root-path:${user.home}/welper-files}")
    private String rootPath;

    @Value("${storage.max-size-bytes:52428800}") // 50MB default
    private long maxSizeBytes;

    private static final Set<String> ALLOWED_STAGES = Set.of(
            "INTAKE", "ASSESSMENT", "PLANNING", "EXECUTION", "RESULT"
    );

    private static final Set<String> ALLOWED_CATEGORIES = Set.of(
            "INTEAKE_RECORD", "INTAKE_RECORD", "PRIVACY_CONSENT", "VISIT_REPORT",
            "SERVICE_PLAN", "CONTRACT", "GENOGRAM", "ECOMAP",
            "VOICE_RECORD", "PROCESS_EVAL", "SATISFACTION_SCAN",
            "TERMINATION_FILE", "OTHER"
    );

    /**
     * 파일 업로드
     */
    @Transactional
    public DocumentUploadResponse upload(MultipartFile file,
                                         String caseNumber,
                                         String stage,
                                         String category) {
        // 기본 null 가드
        Objects.requireNonNull(file, "file");
        Objects.requireNonNull(caseNumber, "caseNumber");
        Objects.requireNonNull(stage, "stage");
        Objects.requireNonNull(category, "category");
        validateFileInput(file, stage, category);

        Client client = clientRepository.findById(caseNumber)
                .orElseThrow(() -> new CustomException(ErrorCode.CLIENT_NOT_FOUND));

        User uploader = getCurrentUser();

        String safeFileName = sanitizeFilename(file.getOriginalFilename());
        String uuid = UUID.randomUUID().toString();
        Path baseDir = Paths.get(rootPath, "cases", caseNumber, stage.toLowerCase());
        Path targetPath = baseDir.resolve(uuid + "_" + safeFileName).normalize();

        // 루트 경로 이탈 방지
        Path normalizedRoot = Paths.get(rootPath).toAbsolutePath().normalize();
        if (!targetPath.toAbsolutePath().normalize().startsWith(normalizedRoot)) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }

        try {
            Files.createDirectories(baseDir);
            Files.write(targetPath, file.getBytes());
        } catch (IOException e) {
            log.error("File upload failed: {}", e.getMessage(), e);
            throw new CustomException(ErrorCode.FILE_UPLOAD_FAILED);
        }

        DocumentFile documentFile = DocumentFile.builder()
                .client(client)
                .stage(stage)
                .category(category)
                .fileName(safeFileName)
                .contentType(file.getContentType())
                .size(file.getSize())
                .storagePath(targetPath.toString())
                .uploader(uploader)
                .status("READY")
                .build();

        DocumentFile saved = Objects.requireNonNull(documentFileRepository.save(documentFile));

        log.info("File uploaded: {} (case: {}, stage: {}, category: {})",
                saved.getId(), caseNumber, stage, category);

        return DocumentUploadResponse.builder()
                .id(saved.getId())
                .fileName(saved.getFileName())
                .contentType(saved.getContentType())
                .size(saved.getSize())
                .stage(saved.getStage())
                .category(saved.getCategory())
                .build();
    }

    /**
     * 문서 목록 조회 (READY 상태)
     */
    public List<DocumentResponse> list(String caseNumber, String stage, String category) {
        List<DocumentFile> files = documentFileRepository
                .findByClient_CaseNumberAndStatus(caseNumber, "READY");

        return files.stream()
                .filter(f -> stage == null || stage.equalsIgnoreCase(f.getStage()))
                .filter(f -> category == null || category.equalsIgnoreCase(f.getCategory()))
                .map(DocumentResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 다운로드용 Resource 조회
     */
    public Resource getFileResource(String documentId) {
        DocumentFile file = documentFileRepository.findById(documentId)
                .orElseThrow(() -> new CustomException(ErrorCode.DOCUMENT_NOT_FOUND));

        if (!"READY".equalsIgnoreCase(file.getStatus())) {
            throw new CustomException(ErrorCode.DOCUMENT_NOT_FOUND);
        }

        if (!StringUtils.hasText(file.getStoragePath())) {
            throw new CustomException(ErrorCode.DOCUMENT_NOT_FOUND);
        }
        Path path = Paths.get(file.getStoragePath());
        if (!Files.exists(path)) {
            throw new CustomException(ErrorCode.DOCUMENT_NOT_FOUND);
        }

        return new FileSystemResource(path);
    }

    /**
     * 문서 단건 조회 (메타데이터)
     */
    public DocumentFile get(String documentId) {
        return documentFileRepository.findById(documentId)
                .orElseThrow(() -> new CustomException(ErrorCode.DOCUMENT_NOT_FOUND));
    }

    /**
     * 삭제 (소프트 삭제 + 물리 파일 삭제)
     */
    @Transactional
    public void delete(String documentId) {
        DocumentFile file = documentFileRepository.findById(documentId)
                .orElseThrow(() -> new CustomException(ErrorCode.DOCUMENT_NOT_FOUND));

        file.setStatus("DELETED");
        documentFileRepository.save(file);

        if (!StringUtils.hasText(file.getStoragePath())) {
            return;
        }
        Path path = Paths.get(file.getStoragePath());
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            log.warn("Failed to delete physical file: {}", path);
        }
    }

    private void validateFileInput(MultipartFile file, String stage, String category) {
        if (file == null || file.isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }
        if (file.getSize() > maxSizeBytes) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }
        if (!StringUtils.hasText(stage) || !ALLOWED_STAGES.contains(stage.toUpperCase())) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }
        if (!StringUtils.hasText(category) || !ALLOWED_CATEGORIES.contains(category.toUpperCase())) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }
    }

    private String sanitizeFilename(String original) {
        String fallback = "file";
        String name = StringUtils.hasText(original) ? original : fallback;
        // allow alphanum, dot, dash, underscore
        return name.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
        String userId = authentication.getName();
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }
}

