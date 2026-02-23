package com.uds.ged.application.service;

import com.uds.ged.application.dto.request.DocumentCreateRequest;
import com.uds.ged.application.dto.request.DocumentStatusRequest;
import com.uds.ged.application.dto.request.DocumentUpdateRequest;
import com.uds.ged.application.dto.response.DocumentResponse;
import com.uds.ged.application.dto.response.DocumentVersionResponse;
import com.uds.ged.application.dto.response.PageResponse;
import com.uds.ged.application.mapper.DocumentMapper;
import com.uds.ged.domain.model.Document;
import com.uds.ged.domain.model.DocumentVersion;
import com.uds.ged.domain.model.User;
import com.uds.ged.domain.model.enums.DocumentStatus;
import com.uds.ged.domain.repository.DocumentRepository;
import com.uds.ged.domain.repository.DocumentVersionRepository;
import com.uds.ged.domain.repository.UserRepository;
import com.uds.ged.infrastructure.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentVersionRepository versionRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;
    private final DocumentMapper documentMapper;

    @Transactional
    public DocumentResponse createDocument(DocumentCreateRequest request, String username) {
        log.debug("Creating document with title: {} and status: {}", request.getTitle(), request.getStatus());

        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        Document document = Document.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .tags(request.getTags())
                .owner(owner)
                .tenantId(request.getTenantId())
                .status(request.getStatus())
                .build();

        Document savedDocument = documentRepository.save(document);
        log.info("Document created with ID: {} and status: {}", savedDocument.getId(), savedDocument.getStatus());

        return documentMapper.toResponse(savedDocument);
    }

    @Transactional(readOnly = true)
    public DocumentResponse getDocumentById(Long id) {
        Document document = documentRepository.findByIdWithTags(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document", "id", id));
        return documentMapper.toResponse(document);
    }

    @Transactional(readOnly = true)
    public PageResponse<DocumentResponse> getAllDocuments(Pageable pageable) {
        Page<Document> documentPage = documentRepository.findAllWithTagsAndOwner(pageable);
        return buildPageResponse(documentPage);
    }

    @Transactional(readOnly = true)
    public PageResponse<DocumentResponse> searchDocuments(String title, DocumentStatus status, Pageable pageable) {
        log.debug("Searching documents with title: {} and status: {}", title, status);
        Page<Document> documentPage = documentRepository.findAll(
            com.uds.ged.domain.specification.DocumentSpecification.withFilters(title, status),
            pageable
        );
        return buildPageResponse(documentPage);
    }

    @Transactional
    public DocumentResponse updateDocument(Long id, DocumentUpdateRequest request) {
        log.debug("Updating document ID: {}", id);

        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document", "id", id));

        if (request.getTitle() != null) {
            document.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            document.setDescription(request.getDescription());
        }
        if (request.getTags() != null) {
            document.setTags(request.getTags());
        }
        if (request.getTenantId() != null) {
            document.setTenantId(request.getTenantId());
        }

        Document updatedDocument = documentRepository.save(document);
        log.info("Document updated: {}", id);

        return documentMapper.toResponse(updatedDocument);
    }

    @Transactional
    public DocumentResponse updateDocumentStatus(Long id, DocumentStatusRequest request) {
        log.debug("Updating document status ID: {} to {}", id, request.getStatus());

        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document", "id", id));

        document.setStatus(request.getStatus());
        Document updatedDocument = documentRepository.save(document);

        log.info("Document status updated: {} to {}", id, request.getStatus());
        return documentMapper.toResponse(updatedDocument);
    }

    @Transactional
    public void deleteDocument(Long id) {
        log.debug("Deleting document ID: {}", id);

        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document", "id", id));

        document.getVersions().forEach(version -> 
            fileStorageService.deleteFile(version.getFileKey())
        );

        documentRepository.delete(document);
        log.info("Document deleted: {}", id);
    }

    @Transactional
    public DocumentVersionResponse uploadVersion(Long documentId, MultipartFile file, String username) {
        log.debug("Uploading new version for document ID: {}", documentId);

        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document", "id", documentId));

        User uploadedBy = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        Integer nextVersionNumber = versionRepository.findMaxVersionNumberByDocumentId(documentId)
                .map(max -> max + 1)
                .orElse(1);

        String fileKey = fileStorageService.storeFile(file, documentId, nextVersionNumber);

        DocumentVersion version = DocumentVersion.builder()
                .document(document)
                .versionNumber(nextVersionNumber)
                .fileKey(fileKey)
                .fileName(file.getOriginalFilename())
                .fileSize(file.getSize())
                .mimeType(file.getContentType())
                .uploadedBy(uploadedBy)
                .build();

        DocumentVersion savedVersion = versionRepository.save(version);
        log.info("Version {} uploaded for document {}", nextVersionNumber, documentId);

        return documentMapper.toVersionResponse(savedVersion);
    }

    @Transactional(readOnly = true)
    public List<DocumentVersionResponse> getDocumentVersions(Long documentId) {
        if (!documentRepository.existsById(documentId)) {
            throw new ResourceNotFoundException("Document", "id", documentId);
        }

        List<DocumentVersion> versions = versionRepository.findByDocumentIdOrderByVersionNumberDesc(documentId);
        return versions.stream()
                .map(documentMapper::toVersionResponse)
                .collect(Collectors.toList());
    }

    private PageResponse<DocumentResponse> buildPageResponse(Page<Document> documentPage) {
        List<DocumentResponse> content = documentPage.getContent().stream()
                .map(documentMapper::toResponse)
                .collect(Collectors.toList());

        return PageResponse.<DocumentResponse>builder()
                .content(content)
                .pageNumber(documentPage.getNumber())
                .pageSize(documentPage.getSize())
                .totalElements(documentPage.getTotalElements())
                .totalPages(documentPage.getTotalPages())
                .last(documentPage.isLast())
                .first(documentPage.isFirst())
                .build();
    }
}
