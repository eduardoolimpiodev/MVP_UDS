package com.uds.ged.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentVersionResponse {

    private Long id;
    private Integer versionNumber;
    private String fileName;
    private Long fileSize;
    private String mimeType;
    private String uploadedBy;
    private LocalDateTime uploadedAt;
}
