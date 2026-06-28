package com.chugalkhorbandar.application.reporting;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.chugalkhorbandar.adapters.persistence.memory.InMemoryDeliveryHistoryRepository;
import com.chugalkhorbandar.adapters.persistence.memory.InMemoryReportArchiveRepository;
import com.chugalkhorbandar.adapters.persistence.memory.InMemoryReportingStore;
import com.chugalkhorbandar.application.email.ReportEmailProperties;
import com.chugalkhorbandar.application.memory.consolidation.MemoryConsolidationReport;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReportingServiceTest {

    @Mock
    private ResendReportDeliveryProvider deliveryProvider;

    private ReportingService service;
    private InMemoryReportingStore store;
    private ReportingProperties properties;

    @BeforeEach
    void setUp() {
        store = new InMemoryReportingStore();
        properties = new ReportingProperties();
        ReportEmailProperties emailProperties = new ReportEmailProperties();
        emailProperties.setEnabled(true);
        emailProperties.setApiKey("test-key");
        emailProperties.setFrom("Bandar <bandar@example.com>");
        emailProperties.setTo("one@example.com");

        ObjectMapper objectMapper = new ObjectMapper();
        ReportingEngine engine = new ReportingEngine(
                properties,
                emailProperties,
                new ReportTemplateLoader(),
                new ReportingTemplateRenderer(),
                new ReportContextBuilder(properties, objectMapper));

        service = new ReportingService(
                properties,
                emailProperties,
                engine,
                deliveryProvider,
                new InMemoryReportArchiveRepository(store),
                new InMemoryDeliveryHistoryRepository(store));
    }

    @Test
    void archivesReportWithAllFormats() {
        MemoryConsolidationReport report = sampleReport();

        ReportArchive archive = service.archiveReport(report);

        assertThat(archive.htmlContent()).isNotBlank();
        assertThat(archive.txtContent()).isNotBlank();
        assertThat(archive.markdownContent()).isNotBlank();
        assertThat(archive.jsonContent()).isNotBlank();
        assertThat(service.getArchive(report.runId())).isPresent();
        assertThat(service.listArchives()).hasSize(1);
    }

    @Test
    void deliversToMultipleRecipientsAndPersistsHistory() {
        ReportEmailProperties multiRecipientEmail = new ReportEmailProperties();
        multiRecipientEmail.setEnabled(true);
        multiRecipientEmail.setApiKey("test-key");
        multiRecipientEmail.setFrom("Bandar <bandar@example.com>");
        multiRecipientEmail.setTo("one@example.com,two@example.com");

        ObjectMapper objectMapper = new ObjectMapper();
        ReportingEngine engine = new ReportingEngine(
                properties,
                multiRecipientEmail,
                new ReportTemplateLoader(),
                new ReportingTemplateRenderer(),
                new ReportContextBuilder(properties, objectMapper));
        ReportingService multiRecipientService = new ReportingService(
                properties,
                multiRecipientEmail,
                engine,
                deliveryProvider,
                new InMemoryReportArchiveRepository(store),
                new InMemoryDeliveryHistoryRepository(store));

        when(deliveryProvider.deliver(any(), eq("one@example.com")))
                .thenReturn(new ResendReportDeliveryProvider.DeliveryAttemptResult("SENT", "msg-1", 10, ""));
        when(deliveryProvider.deliver(any(), eq("two@example.com")))
                .thenReturn(new ResendReportDeliveryProvider.DeliveryAttemptResult("FAILED", "", 20, "timeout"));

        ReportingDeliverySummary summary = multiRecipientService.processReport(sampleReport());

        assertThat(summary.status()).isEqualTo("PARTIAL");
        assertThat(summary.recipientsSent()).isEqualTo(1);
        assertThat(summary.recipientsFailed()).isEqualTo(1);
        assertThat(multiRecipientService.getDeliveryHistory()).hasSize(2);
    }

    @Test
    void previewRequiresPreviewEnabled() {
        properties.setPreviewEnabled(false);
        assertThatThrownBy(() -> service.previewHtml(sampleReport()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("preview");
    }

    @Test
    void recordsFailedDeliveryForRetryEligibility() {
        when(deliveryProvider.deliver(any(), eq("one@example.com")))
                .thenReturn(new ResendReportDeliveryProvider.DeliveryAttemptResult("FAILED", "", 5, "fail"));

        service.processReport(sampleReport());

        assertThat(service.getDeliveryHistory()).hasSize(1);
        assertThat(service.getDeliveryHistory().get(0).status()).isEqualTo("FAILED");
        assertThat(service.getDeliveryHistory().get(0).attempt()).isEqualTo(1);
    }

    private static MemoryConsolidationReport sampleReport() {
        Instant now = Instant.parse("2026-06-01T06:00:00Z");
        return new MemoryConsolidationReport(
                "run-service-1",
                now,
                now,
                50,
                2,
                1,
                0,
                0,
                1,
                0,
                1,
                "Summary",
                "TXT",
                "{\"date\":\"2026-06-01\",\"promoted\":1}",
                "",
                "PENDING",
                "");
    }
}
