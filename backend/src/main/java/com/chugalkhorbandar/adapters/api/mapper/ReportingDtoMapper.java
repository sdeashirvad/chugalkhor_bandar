package com.chugalkhorbandar.adapters.api.mapper;

import com.chugalkhorbandar.adapters.api.dto.DeliveryHistoryResponseDto;
import com.chugalkhorbandar.adapters.api.dto.ReportArchiveResponseDto;
import com.chugalkhorbandar.adapters.api.dto.ReportingConfigurationResponseDto;
import com.chugalkhorbandar.application.reporting.DeliveryHistory;
import com.chugalkhorbandar.application.reporting.ReportArchive;
import com.chugalkhorbandar.application.reporting.ReportingService;

public final class ReportingDtoMapper {

    private ReportingDtoMapper() {}

    public static DeliveryHistoryResponseDto toDto(DeliveryHistory entry) {
        return new DeliveryHistoryResponseDto(
                entry.id(),
                entry.reportId(),
                entry.recipient(),
                entry.status(),
                entry.provider(),
                entry.providerMessageId(),
                entry.attempt(),
                entry.latencyMs(),
                entry.error(),
                entry.createdAt());
    }

    public static ReportArchiveResponseDto toDto(ReportArchive archive) {
        return new ReportArchiveResponseDto(
                archive.reportId(),
                archive.htmlContent(),
                archive.txtContent(),
                archive.jsonContent(),
                archive.markdownContent(),
                archive.createdAt());
    }

    public static ReportingConfigurationResponseDto toDto(ReportingService.ReportingConfigurationView view) {
        return new ReportingConfigurationResponseDto(
                view.enabled(),
                view.archiveEnabled(),
                view.retryEnabled(),
                view.previewEnabled(),
                view.maxRetries(),
                view.subjectTemplate(),
                view.sender(),
                view.recipients(),
                view.closings(),
                view.attachments(),
                view.emailEnabled());
    }
}
