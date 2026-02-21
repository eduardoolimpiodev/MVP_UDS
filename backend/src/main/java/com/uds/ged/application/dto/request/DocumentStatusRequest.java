package com.uds.ged.application.dto.request;

import com.uds.ged.domain.model.enums.DocumentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentStatusRequest {

    @NotNull(message = "Status is required")
    private DocumentStatus status;
}
