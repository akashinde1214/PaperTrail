package com.papertrail.dto;

public record AuthResponse(
        String token,
        UserProfileResponse user
) {
}
