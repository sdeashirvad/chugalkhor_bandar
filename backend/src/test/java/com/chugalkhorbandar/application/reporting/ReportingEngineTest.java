package com.chugalkhorbandar.application.reporting;

import static org.assertj.core.api.Assertions.assertThat;

import com.chugalkhorbandar.adapters.persistence.memory.InMemoryDeliveryHistoryRepository;
import com.chugalkhorbandar.adapters.persistence.memory.InMemoryReportArchiveRepository;
import com.chugalkhorbandar.adapters.persistence.memory.InMemoryReportingStore;
import com.chugalkhorbandar.application.email.ReportEmailProperties;
import com.chugalkhorbandar.application.memory.consolidation.MemoryConsolidationReport;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReportingEngineTest {

    private ReportingEngine engine;

    @BeforeEach
    void setUp() {
        ReportingProperties properties = new ReportingProperties();
        ReportEmailProperties emailProperties = new ReportEmailProperties();
        ObjectMapper objectMapper = new ObjectMapper();
        engine = new ReportingEngine(
                properties,
                emailProperties,
                new ReportTemplateLoader(),
                new ReportingTemplateRenderer(),
                new ReportContextBuilder(properties, objectMapper));
    }

    @Test
    void buildsHtmlTxtMarkdownAndJsonArchive() {
        ReportArchive archive = engine.buildArchive(sampleReport());

        assertThat(archive.htmlContent()).contains("Good morning, Ashirvad");
        assertThat(archive.htmlContent()).contains("<html");
        assertThat(archive.txtContent()).contains("Good morning, Ashirvad");
        assertThat(archive.markdownContent()).contains("Good morning, Ashirvad");
        assertThat(archive.jsonContent()).contains("\"promoted\":2");
    }

    @Test
    void buildsEmailPayloadWithSubjectAndRecipients() {
        ReportingProperties properties = new ReportingProperties();
        ReportEmailProperties emailProperties = new ReportEmailProperties();
        emailProperties.setFrom("Bandar <bandar@ashirvad.work>");
        emailProperties.setTo("a@gmail.com,b@gmail.com");
        emailProperties.setSubjectTemplate("Letter {date} — {promoted} promoted");

        ObjectMapper objectMapper = new ObjectMapper();
        ReportingEngine configuredEngine = new ReportingEngine(
                properties,
                emailProperties,
                new ReportTemplateLoader(),
                new ReportingTemplateRenderer(),
                new ReportContextBuilder(properties, objectMapper));

        MemoryConsolidationReport report = sampleReport();
        ReportArchive archive = configuredEngine.buildArchive(report);
        ReportingEmailPayload payload = configuredEngine.buildEmailPayload(report, archive);

        assertThat(payload.from()).isEqualTo("Bandar <bandar@ashirvad.work>");
        assertThat(payload.recipients()).containsExactly("a@gmail.com", "b@gmail.com");
        assertThat(payload.subject()).contains("2026-06-01");
        assertThat(payload.subject()).contains("2 promoted");
        assertThat(payload.attachments()).isNotEmpty();
    }

    private static MemoryConsolidationReport sampleReport() {
        Instant now = Instant.parse("2026-06-01T06:00:00Z");
        String json =
                """
                {"date":"2026-06-01","conversations":"3","artifacts":"2","inboxItems":"5","promoted":2,"discarded":1,"candidates":1,"pendingPromises":"0","unreadNotifications":"1"}
                """;
        return new MemoryConsolidationReport(
                "run-report-1",
                now,
                now,
                100,
                5,
                2,
                1,
                0,
                3,
                1,
                1,
                "Summary",
                "TXT body",
                json,
                "The jungle whispered many secrets today.",
                "SKIPPED",
                "");
    }
}
