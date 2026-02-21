package com.uds.ged.presentation.controller;

import com.uds.ged.application.dto.request.DocumentCreateRequest;
import com.uds.ged.application.dto.request.DocumentStatusRequest;
import com.uds.ged.application.dto.request.DocumentUpdateRequest;
import com.uds.ged.application.dto.response.ApiResponse;
import com.uds.ged.application.dto.response.DocumentResponse;
import com.uds.ged.application.dto.response.DocumentVersionResponse;
import com.uds.ged.application.dto.response.PageResponse;
import com.uds.ged.application.service.DocumentService;
import com.uds.ged.domain.model.enums.DocumentStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping
    public ResponseEntity<ApiResponse<DocumentResponse>> createDocument(
            @Valid @RequestBody DocumentCreateRequest request,
            Authentication authentication) {
        DocumentResponse response = documentService.createDocument(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Document created successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DocumentResponse>> getDocument(@PathVariable Long id) {
        DocumentResponse response = documentService.getDocumentById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<DocumentResponse>>> getAllDocuments(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) DocumentStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {

        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        PageResponse<DocumentResponse> response = (title != null || status != null)
                ? documentService.searchDocuments(title, status, pageable)
                : documentService.getAllDocuments(pageable);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DocumentResponse>> updateDocument(
            @PathVariable Long id,
            @Valid @RequestBody DocumentUpdateRequest request) {
        DocumentResponse response = documentService.updateDocument(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Document updated successfully"));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<DocumentResponse>> updateDocumentStatus(
            @PathVariable Long id,
            @Valid @RequestBody DocumentStatusRequest request) {
        DocumentResponse response = documentService.updateDocumentStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Document status updated successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteDocument(@PathVariable Long id) {
        documentService.deleteDocument(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Document deleted successfully"));
    }

    @PostMapping("/{id}/versions")
    public ResponseEntity<ApiResponse<DocumentVersionResponse>> uploadVersion(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {
        DocumentVersionResponse response = documentService.uploadVersion(id, file, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Version uploaded successfully"));
    }

    @GetMapping("/{id}/versions")
    public ResponseEntity<ApiResponse<List<DocumentVersionResponse>>> getDocumentVersions(@PathVariable Long id) {
        List<DocumentVersionResponse> response = documentService.getDocumentVersions(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
