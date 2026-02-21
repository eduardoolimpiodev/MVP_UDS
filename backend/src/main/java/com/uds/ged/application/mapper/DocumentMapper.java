package com.uds.ged.application.mapper;

import com.uds.ged.application.dto.response.DocumentResponse;
import com.uds.ged.application.dto.response.DocumentVersionResponse;
import com.uds.ged.domain.model.Document;
import com.uds.ged.domain.model.DocumentVersion;
import org.springframework.stereotype.Component;

@Component
public class DocumentMapper {

    public DocumentResponse toResponse(Document document) {
        Integer currentVersion = document.getVersions().isEmpty() ? null :
                document.getVersions().get(0).getVersionNumber();

        return DocumentResponse.builder()
                .id(document.getId())
                .title(document.getTitle())
                .description(document.getDescription())
                .tags(document.getTags())
                .ownerUsername(document.getOwner().getUsername())
                .tenantId(document.getTenantId())
                .status(document.getStatus())
                .currentVersion(currentVersion)
                .createdAt(document.getCreatedAt())
                .updatedAt(document.getUpdatedAt())
                .build();
    }

    public DocumentVersionResponse toVersionResponse(DocumentVersion version) {
        return DocumentVersionResponse.builder()
                .id(version.getId())
                .versionNumber(version.getVersionNumber())
                .fileName(version.getFileName())
                .fileSize(version.getFileSize())
                .mimeType(version.getMimeType())
                .uploadedBy(version.getUploadedBy().getUsername())
                .uploadedAt(version.getUploadedAt())
                .build();
    }
}
