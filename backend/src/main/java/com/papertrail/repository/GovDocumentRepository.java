package com.papertrail.repository;

import com.papertrail.model.DocType;
import com.papertrail.model.GovDocument;
import com.papertrail.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface GovDocumentRepository extends JpaRepository<GovDocument, Long> {
    List<GovDocument> findByUser(User user);
    List<GovDocument> findByUserAndDocType(User user, DocType docType);
    List<GovDocument> findByUserAndExpiryDateLessThanEqual(User user, LocalDate date);
    Optional<GovDocument> findByIdAndUser(Long id, User user);
}
