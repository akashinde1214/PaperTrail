package com.papertrail.repository;

import com.papertrail.model.OtpChannel;
import com.papertrail.model.OtpCode;
import com.papertrail.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface OtpCodeRepository extends JpaRepository<OtpCode, Long> {
    Optional<OtpCode> findTopByUserAndPurposeAndChannelAndUsedFalseOrderByCreatedAtDesc(
            User user,
            String purpose,
            OtpChannel channel
    );

    long deleteByExpiresAtBefore(LocalDateTime cutoff);
}
