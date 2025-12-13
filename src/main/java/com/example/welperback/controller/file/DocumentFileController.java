package com.example.welperback.controller.file;

import com.example.welperback.domain.file.DocumentFile;
import com.example.welperback.dto.file.DocumentResponse;
import com.example.welperback.dto.file.DocumentUploadResponse;
import com.example.welperback.global.response.ApiResponse;
import com.example.welperback.service.file.DocumentFileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "DocumentFile", description = "문서/파일 업로드 및 다운로드")
@SuppressWarnings({"NullAway", "null", "ConstantConditions"})
public class DocumentFileController {

    private final DocumentFileService documentFileService;

    @PostMapping(
            value = "/files/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @Operation(
            summary = "파일 업로드",
            description = "caseNumber, stage, category를 전달하고 멀티파트 파일을 업로드합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ApiResponse<DocumentUploadResponse> upload(
            @RequestPart("file") MultipartFile file,
            @RequestParam("caseNumber") String caseNumber,
            @RequestParam("stage") String stage,
            @RequestParam("category") String category
    ) {
        DocumentUploadResponse response = documentFileService.upload(file, caseNumber, stage.toUpperCase(), category.toUpperCase());
        return ApiResponse.success(response);
    }

    @GetMapping("/cases/{caseNumber}/documents")
    @Operation(
            summary = "문서 목록 조회",
            description = "해당 사례의 문서 목록을 조회합니다. stage/category로 필터 가능.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ApiResponse<List<DocumentResponse>> list(
            @PathVariable String caseNumber,
            @RequestParam(value = "stage", required = false) String stage,
            @RequestParam(value = "category", required = false) String category
    ) {
        List<DocumentResponse> responses = documentFileService.list(
                caseNumber,
                stage != null ? stage.toUpperCase() : null,
                category != null ? category.toUpperCase() : null
        );
        return ApiResponse.success(responses);
    }

    @GetMapping("/documents/{id}/download")
    @Operation(
            summary = "파일 다운로드",
            description = "문서를 다운로드합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<Resource> download(@PathVariable String id) {
        DocumentFile file = documentFileService.get(id);
        Resource resource = documentFileService.getFileResource(id);

        String downloadName = file.getFileName() != null ? file.getFileName() : "download";
        String encodedName = URLEncoder.encode(downloadName, StandardCharsets.UTF_8);
        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
        if (file.getContentType() != null) {
            try {
                mediaType = MediaType.parseMediaType(file.getContentType());
            } catch (Exception ignored) {}
        }

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedName)
                .body(resource);
    }

    @DeleteMapping("/documents/{id}")
    @Operation(
            summary = "파일 삭제",
            description = "문서를 논리 삭제하고 물리 파일도 제거합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ApiResponse<String> delete(@PathVariable String id) {
        documentFileService.delete(id);
        return ApiResponse.success("삭제되었습니다.");
    }
}

