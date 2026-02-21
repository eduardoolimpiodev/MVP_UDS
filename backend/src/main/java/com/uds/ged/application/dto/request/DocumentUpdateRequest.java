package com.uds.ged.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentUpdateRequest {

    private String title;

    private String description;

    private List<String> tags;

    private String tenantId;
}
