package com.uds.ged.presentation.controller;

import com.uds.ged.application.service.FileStorageService;
import com.uds.ged.domain.model.DocumentVersion;
import com.uds.ged.domain.repository.DocumentVersionRepository;
import com.uds.ged.infrastructure.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileStorageService fileStorageService;
    private final DocumentVersionRepository versionRepository;

    @GetMapping("/{versionId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long versionId) {
        DocumentVersion version = versionRepository.findById(versionId)
                .orElseThrow(() -> new ResourceNotFoundException("DocumentVersion", "id", versionId));

        Resource resource = fileStorageService.loadFileAsResource(version.getFileKey());

        String contentType = version.getMimeType();
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + version.getFileName() + "\"")
                .body(resource);
    }
}
