package com.papertrail.dto;

public record AlertSummaryResponse(
        long total,
        long expired,
        long critical,
        long warning,
        long safe
) {
}
