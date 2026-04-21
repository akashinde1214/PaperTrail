package com.papertrail.dto;

import com.papertrail.model.AlertLevel;
import com.papertrail.model.DocType;
import com.papertrail.model.GovDocument;

import java.time.LocalDate;

public record DocumentResponse(
        Long id,
        DocType docType,
        String name,
        String documentNumber,
        LocalDate issueDate,
        LocalDate expiryDate,
        Integer renewalCycleDays,
        String notes,
        long daysToExpiry,
        AlertLevel alertLevel
) {
    public static DocumentResponse from(GovDocument doc, long daysToExpiry, AlertLevel alertLevel) {
        return new DocumentResponse(
                doc.getId(),
                doc.getDocType(),
                doc.getName(),
                doc.getDocumentNumber(),
                doc.getIssueDate(),
                doc.getExpiryDate(),
                doc.getRenewalCycleDays(),
                doc.getNotes(),
                daysToExpiry,
                alertLevel
        );
    }
}
