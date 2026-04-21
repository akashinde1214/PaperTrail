package com.papertrail.service;

import com.papertrail.dto.AlertSummaryResponse;
import com.papertrail.dto.DocumentRequest;
import com.papertrail.dto.DocumentResponse;
import com.papertrail.model.AlertLevel;
import com.papertrail.model.DocType;
import com.papertrail.model.GovDocument;
import com.papertrail.model.User;
import com.papertrail.repository.GovDocumentRepository;
import com.papertrail.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

@Service
public class DocumentService {

    private final GovDocumentRepository govDocumentRepository;
    private final UserRepository userRepository;

    public DocumentService(GovDocumentRepository govDocumentRepository, UserRepository userRepository) {
        this.govDocumentRepository = govDocumentRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public DocumentResponse create(Long userId, DocumentRequest request) {
        User user = getUser(userId);

        GovDocument doc = new GovDocument();
        doc.setUser(user);
        doc.setDocType(request.docType());
        doc.setName(request.name().trim());
        doc.setDocumentNumber(request.documentNumber().trim());
        doc.setIssueDate(request.issueDate());

        int renewalDays = request.renewalCycleDays() != null
                ? request.renewalCycleDays()
                : request.docType().getDefaultRenewalCycleDays();
        doc.setRenewalCycleDays(renewalDays);

        LocalDate expiry = request.expiryDate() != null
                ? request.expiryDate()
                : request.issueDate().plusDays(renewalDays);
        if (expiry.isBefore(request.issueDate())) {
            throw new IllegalArgumentException("Expiry date cannot be before issue date");
        }

        doc.setExpiryDate(expiry);
        doc.setNotes(request.notes() == null ? null : request.notes().trim());

        GovDocument saved = govDocumentRepository.save(doc);
        return toResponse(saved);
    }

    @Transactional
    public DocumentResponse update(Long userId, Long docId, DocumentRequest request) {
        User user = getUser(userId);
        GovDocument existing = govDocumentRepository.findByIdAndUser(docId, user)
                .orElseThrow(() -> new IllegalArgumentException("Document not found"));

        existing.setDocType(request.docType());
        existing.setName(request.name().trim());
        existing.setDocumentNumber(request.documentNumber().trim());
        existing.setIssueDate(request.issueDate());

        int renewalDays = request.renewalCycleDays() != null
                ? request.renewalCycleDays()
                : request.docType().getDefaultRenewalCycleDays();
        existing.setRenewalCycleDays(renewalDays);

        LocalDate expiry = request.expiryDate() != null
                ? request.expiryDate()
                : request.issueDate().plusDays(renewalDays);
        if (expiry.isBefore(request.issueDate())) {
            throw new IllegalArgumentException("Expiry date cannot be before issue date");
        }

        existing.setExpiryDate(expiry);
        existing.setNotes(request.notes() == null ? null : request.notes().trim());

        return toResponse(govDocumentRepository.save(existing));
    }

    public List<DocumentResponse> list(Long userId, DocType docType) {
        User user = getUser(userId);
        List<GovDocument> docs = docType == null
                ? govDocumentRepository.findByUser(user)
                : govDocumentRepository.findByUserAndDocType(user, docType);

        PriorityQueue<GovDocument> queue = new PriorityQueue<>(Comparator.comparing(GovDocument::getExpiryDate));
        queue.addAll(docs);

        return queue.stream()
                .sorted(Comparator.comparing(GovDocument::getExpiryDate))
                .map(this::toResponse)
                .toList();
    }

    public DocumentResponse getById(Long userId, Long docId) {
        User user = getUser(userId);
        GovDocument doc = govDocumentRepository.findByIdAndUser(docId, user)
                .orElseThrow(() -> new IllegalArgumentException("Document not found"));
        return toResponse(doc);
    }

    @Transactional
    public void delete(Long userId, Long docId) {
        User user = getUser(userId);
        GovDocument doc = govDocumentRepository.findByIdAndUser(docId, user)
                .orElseThrow(() -> new IllegalArgumentException("Document not found"));
        govDocumentRepository.delete(doc);
    }

    public AlertSummaryResponse getAlertSummary(Long userId) {
        User user = getUser(userId);
        List<GovDocument> docs = govDocumentRepository.findByUser(user);

        long expired = docs.stream().filter(d -> classifyAlert(daysToExpiry(d.getExpiryDate())) == AlertLevel.EXPIRED).count();
        long critical = docs.stream().filter(d -> classifyAlert(daysToExpiry(d.getExpiryDate())) == AlertLevel.CRITICAL).count();
        long warning = docs.stream().filter(d -> classifyAlert(daysToExpiry(d.getExpiryDate())) == AlertLevel.WARNING).count();
        long safe = docs.stream().filter(d -> classifyAlert(daysToExpiry(d.getExpiryDate())) == AlertLevel.SAFE).count();

        return new AlertSummaryResponse(docs.size(), expired, critical, warning, safe);
    }

    private DocumentResponse toResponse(GovDocument doc) {
        long days = daysToExpiry(doc.getExpiryDate());
        AlertLevel alertLevel = classifyAlert(days);
        return DocumentResponse.from(doc, days, alertLevel);
    }

    private long daysToExpiry(LocalDate expiryDate) {
        return ChronoUnit.DAYS.between(LocalDate.now(), expiryDate);
    }

    private AlertLevel classifyAlert(long daysToExpiry) {
        if (daysToExpiry < 0) {
            return AlertLevel.EXPIRED;
        }
        if (daysToExpiry <= 30) {
            return AlertLevel.CRITICAL;
        }
        if (daysToExpiry <= 90) {
            return AlertLevel.WARNING;
        }
        return AlertLevel.SAFE;
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}
