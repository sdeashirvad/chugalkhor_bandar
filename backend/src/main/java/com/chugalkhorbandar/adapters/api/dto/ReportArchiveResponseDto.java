package com.chugalkhorbandar.adapters.api.dto;

import java.time.Instant;

public record ReportArchiveResponseDto(
        String reportId,
        String htmlContent,
        String txtContent,
        String jsonContent,
        String markdownContent,
        Instant createdAt) {}
