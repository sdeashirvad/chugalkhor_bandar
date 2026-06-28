package com.chugalkhorbandar.adapters.api.dto;

import com.chugalkhorbandar.application.reporting.ReportingAttachmentProperties;
import java.util.List;

public record ReportingConfigurationResponseDto(
        boolean enabled,
        boolean archiveEnabled,
        boolean retryEnabled,
        boolean previewEnabled,
        int maxRetries,
        String subjectTemplate,
        String sender,
        List<String> recipients,
        List<String> closings,
        ReportingAttachmentProperties attachments,
        boolean emailEnabled) {}
