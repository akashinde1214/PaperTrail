package com.papertrail.dto;

import com.papertrail.model.OtpChannel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OtpSendRequest(
        @NotBlank(message = "Purpose is required")
        String purpose,

        @NotNull(message = "Channel is required")
        OtpChannel channel
) {
}
