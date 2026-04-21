package com.papertrail.dto;

import com.papertrail.model.OtpChannel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record OtpVerifyRequest(
        @NotBlank(message = "Purpose is required")
        String purpose,

        @NotNull(message = "Channel is required")
        OtpChannel channel,

        @NotBlank(message = "OTP code is required")
        @Pattern(regexp = "^\\d{6}$", message = "OTP must be a 6-digit code")
        String code
) {
}
