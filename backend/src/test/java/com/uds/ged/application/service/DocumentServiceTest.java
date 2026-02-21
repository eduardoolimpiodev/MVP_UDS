package com.uds.ged.application.service;

import com.uds.ged.application.dto.request.DocumentCreateRequest;
import com.uds.ged.application.dto.request.DocumentStatusRequest;
import com.uds.ged.application.dto.response.DocumentResponse;
import com.uds.ged.application.mapper.DocumentMapper;
import com.uds.ged.domain.model.Document;
import com.uds.ged.domain.model.User;
import com.uds.ged.domain.model.enums.DocumentStatus;
import com.uds.ged.domain.model.enums.UserRole;
import com.uds.ged.domain.repository.DocumentRepository;
import com.uds.ged.domain.repository.UserRepository;
import com.uds.ged.infrastructure.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DocumentMapper documentMapper;

    @InjectMocks
    private DocumentService documentService;

    private User testUser;
    private Document testDocument;
    private DocumentCreateRequest createRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .role(UserRole.USER)
                .build();

        testDocument = Document.builder()
                .id(1L)
                .title("Test Document")
                .description("Test Description")
                .tags(Arrays.asList("tag1", "tag2"))
                .owner(testUser)
                .status(DocumentStatus.DRAFT)
                .build();

        createRequest = new DocumentCreateRequest(
                "Test Document",
                "Test Description",
                Arrays.asList("tag1", "tag2"),
                "tenant1"
        );
    }

    @Test
    @DisplayName("Should create document successfully")
    void shouldCreateDocumentSuccessfully() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(documentRepository.save(any(Document.class))).thenReturn(testDocument);
        when(documentMapper.toResponse(testDocument)).thenReturn(
                DocumentResponse.builder()
                        .id(1L)
                        .title("Test Document")
                        .status(DocumentStatus.DRAFT)
                        .build()
        );

        DocumentResponse response = documentService.createDocument(createRequest, "testuser");

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getTitle()).isEqualTo("Test Document");
        assertThat(response.getStatus()).isEqualTo(DocumentStatus.DRAFT);

        verify(userRepository, times(1)).findByUsername("testuser");
        verify(documentRepository, times(1)).save(any(Document.class));
    }

    @Test
    @DisplayName("Should throw exception when user not found during document creation")
    void shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> documentService.createDocument(createRequest, "nonexistent"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found");

        verify(documentRepository, never()).save(any(Document.class));
    }

    @Test
    @DisplayName("Should update document status successfully")
    void shouldUpdateDocumentStatusSuccessfully() {
        DocumentStatusRequest statusRequest = new DocumentStatusRequest(DocumentStatus.PUBLISHED);
        
        when(documentRepository.findById(1L)).thenReturn(Optional.of(testDocument));
        when(documentRepository.save(any(Document.class))).thenReturn(testDocument);
        when(documentMapper.toResponse(testDocument)).thenReturn(
                DocumentResponse.builder()
                        .id(1L)
                        .status(DocumentStatus.PUBLISHED)
                        .build()
        );

        DocumentResponse response = documentService.updateDocumentStatus(1L, statusRequest);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(DocumentStatus.PUBLISHED);
        assertThat(testDocument.getStatus()).isEqualTo(DocumentStatus.PUBLISHED);

        verify(documentRepository, times(1)).findById(1L);
        verify(documentRepository, times(1)).save(testDocument);
    }

    @Test
    @DisplayName("Should throw exception when document not found")
    void shouldThrowExceptionWhenDocumentNotFound() {
        when(documentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> documentService.getDocumentById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Document not found");

        verify(documentRepository, times(1)).findById(999L);
    }
}
