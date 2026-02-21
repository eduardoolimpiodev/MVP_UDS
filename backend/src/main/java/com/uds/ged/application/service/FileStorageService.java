package com.uds.ged.application.service;

import com.uds.ged.infrastructure.exception.FileStorageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageService {

    private final Path fileStorageLocation;

    public FileStorageService(@Value("${app.storage.location}") String storageLocation) {
        this.fileStorageLocation = Paths.get(storageLocation).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
            log.info("File storage location initialized: {}", this.fileStorageLocation);
        } catch (IOException ex) {
            throw new FileStorageException("Could not create storage directory", ex);
        }
    }

    public String storeFile(MultipartFile file, Long documentId, Integer versionNumber) {
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        
        try {
            if (originalFilename.contains("..")) {
                throw new FileStorageException("Invalid file path: " + originalFilename);
            }

            String fileExtension = "";
            int dotIndex = originalFilename.lastIndexOf('.');
            if (dotIndex > 0) {
                fileExtension = originalFilename.substring(dotIndex);
            }

            String fileName = String.format("doc_%d_v%d_%s%s",
                    documentId,
                    versionNumber,
                    UUID.randomUUID().toString(),
                    fileExtension);

            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            log.info("File stored successfully: {}", fileName);
            return fileName;

        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + originalFilename, ex);
        }
    }

    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new FileStorageException("File not found: " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new FileStorageException("File not found: " + fileName, ex);
        }
    }

    public void deleteFile(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Files.deleteIfExists(filePath);
            log.info("File deleted: {}", fileName);
        } catch (IOException ex) {
            log.error("Could not delete file: {}", fileName, ex);
        }
    }
}
