package com.uds.ged.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentCreateRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    private List<String> tags = new ArrayList<>();

    private String tenantId;
}
