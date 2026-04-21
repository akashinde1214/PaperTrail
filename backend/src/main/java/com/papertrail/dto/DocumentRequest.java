package com.papertrail.dto;

import com.papertrail.model.DocType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record DocumentRequest(
        @NotNull(message = "Document type is required")
        DocType docType,

        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Document number is required")
        String documentNumber,

        @NotNull(message = "Issue date is required")
        @PastOrPresent(message = "Issue date cannot be in the future")
        LocalDate issueDate,

        LocalDate expiryDate,

        @Positive(message = "Renewal cycle must be positive")
        Integer renewalCycleDays,

        String notes
) {
}
