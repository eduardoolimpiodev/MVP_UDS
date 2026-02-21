package com.uds.ged.application.service;

import com.uds.ged.infrastructure.exception.FileStorageException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileStorageServiceTest {

    @TempDir
    Path tempDir;

    private FileStorageService fileStorageService;

    @BeforeEach
    void setUp() {
        fileStorageService = new FileStorageService(tempDir.toString());
    }

    @Test
    @DisplayName("Should store file successfully")
    void shouldStoreFileSuccessfully() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "Test content".getBytes()
        );

        String fileKey = fileStorageService.storeFile(file, 1L, 1);

        assertThat(fileKey).isNotNull();
        assertThat(fileKey).contains("doc_1_v1");
        assertThat(fileKey).endsWith(".pdf");

        Path storedFile = tempDir.resolve(fileKey);
        assertThat(Files.exists(storedFile)).isTrue();
        assertThat(Files.readString(storedFile)).isEqualTo("Test content");
    }

    @Test
    @DisplayName("Should load file as resource successfully")
    void shouldLoadFileAsResourceSuccessfully() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "Test content".getBytes()
        );

        String fileKey = fileStorageService.storeFile(file, 1L, 1);
        Resource resource = fileStorageService.loadFileAsResource(fileKey);

        assertThat(resource).isNotNull();
        assertThat(resource.exists()).isTrue();
        assertThat(resource.isReadable()).isTrue();
    }

    @Test
    @DisplayName("Should throw exception when loading non-existent file")
    void shouldThrowExceptionWhenLoadingNonExistentFile() {
        assertThatThrownBy(() -> fileStorageService.loadFileAsResource("nonexistent.pdf"))
                .isInstanceOf(FileStorageException.class)
                .hasMessageContaining("File not found");
    }

    @Test
    @DisplayName("Should throw exception for invalid file path")
    void shouldThrowExceptionForInvalidFilePath() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "../../../etc/passwd",
                "text/plain",
                "malicious content".getBytes()
        );

        assertThatThrownBy(() -> fileStorageService.storeFile(file, 1L, 1))
                .isInstanceOf(FileStorageException.class)
                .hasMessageContaining("Invalid file path");
    }

    @Test
    @DisplayName("Should delete file successfully")
    void shouldDeleteFileSuccessfully() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "Test content".getBytes()
        );

        String fileKey = fileStorageService.storeFile(file, 1L, 1);
        Path storedFile = tempDir.resolve(fileKey);
        
        assertThat(Files.exists(storedFile)).isTrue();

        fileStorageService.deleteFile(fileKey);

        assertThat(Files.exists(storedFile)).isFalse();
    }
}
