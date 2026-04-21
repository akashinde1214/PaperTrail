package com.papertrail.controller;

import com.papertrail.dto.AlertSummaryResponse;
import com.papertrail.dto.DocumentRequest;
import com.papertrail.dto.DocumentResponse;
import com.papertrail.model.DocType;
import com.papertrail.security.AuthUserPrincipal;
import com.papertrail.service.DocumentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DocumentResponse create(
            @AuthenticationPrincipal AuthUserPrincipal principal,
            @Valid @RequestBody DocumentRequest request
    ) {
        return documentService.create(principal.getUserId(), request);
    }

    @PutMapping("/{id}")
    public DocumentResponse update(
            @AuthenticationPrincipal AuthUserPrincipal principal,
            @PathVariable Long id,
            @Valid @RequestBody DocumentRequest request
    ) {
        return documentService.update(principal.getUserId(), id, request);
    }

    @GetMapping
    public List<DocumentResponse> list(
            @AuthenticationPrincipal AuthUserPrincipal principal,
            @RequestParam(required = false) DocType docType
    ) {
        return documentService.list(principal.getUserId(), docType);
    }

    @GetMapping("/{id}")
    public DocumentResponse getById(
            @AuthenticationPrincipal AuthUserPrincipal principal,
            @PathVariable Long id
    ) {
        return documentService.getById(principal.getUserId(), id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @AuthenticationPrincipal AuthUserPrincipal principal,
            @PathVariable Long id
    ) {
        documentService.delete(principal.getUserId(), id);
    }

    @GetMapping("/alerts/summary")
    public AlertSummaryResponse alertSummary(@AuthenticationPrincipal AuthUserPrincipal principal) {
        return documentService.getAlertSummary(principal.getUserId());
    }
}
