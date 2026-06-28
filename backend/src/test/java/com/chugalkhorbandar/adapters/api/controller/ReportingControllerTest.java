package com.chugalkhorbandar.adapters.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.chugalkhorbandar.adapters.api.ApiExceptionHandler;
import com.chugalkhorbandar.application.memory.consolidation.MemoryConsolidationReport;
import com.chugalkhorbandar.application.memory.consolidation.MemoryConsolidationService;
import com.chugalkhorbandar.application.reporting.DeliveryHistory;
import com.chugalkhorbandar.application.reporting.ReportArchive;
import com.chugalkhorbandar.application.reporting.ReportingAttachmentProperties;
import com.chugalkhorbandar.application.reporting.ReportingDeliverySummary;
import com.chugalkhorbandar.application.reporting.ReportingService;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = ReportingController.class)
@Import(ApiExceptionHandler.class)
class ReportingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReportingService reportingService;

    @MockitoBean
    private MemoryConsolidationService consolidationService;

    @Test
    void previewHtmlReturnsContent() throws Exception {
        when(consolidationService.getLatestReport()).thenReturn(Optional.of(sampleReport()));
        when(reportingService.previewHtml(any())).thenReturn("<html>preview</html>");

        mockMvc.perform(get("/api/reporting/preview/html"))
                .andExpect(status().isOk())
                .andExpect(content().string("<html>preview</html>"));
    }

    @Test
    void sendTestEmailReturnsSummary() throws Exception {
        when(consolidationService.getLatestReport()).thenReturn(Optional.of(sampleReport()));
        when(reportingService.sendTestEmail(any()))
                .thenReturn(new ReportingDeliverySummary("SENT", "", 2, 0));

        mockMvc.perform(post("/api/reporting/send-test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SENT"))
                .andExpect(jsonPath("$.recipientsSent").value(2));
    }

    @Test
    void historyReturnsEntries() throws Exception {
        Instant now = Instant.parse("2026-06-01T06:00:00Z");
        when(reportingService.getDeliveryHistory())
                .thenReturn(List.of(new DeliveryHistory(
                        "h-1", "run-1", "a@gmail.com", "SENT", "resend", "msg-1", 1, 12, "", now)));

        mockMvc.perform(get("/api/reporting/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].recipient").value("a@gmail.com"));
    }

    @Test
    void archiveReturnsStoredReport() throws Exception {
        Instant now = Instant.parse("2026-06-01T06:00:00Z");
        when(reportingService.getArchive("run-1"))
                .thenReturn(Optional.of(new ReportArchive("run-1", "<html>", "txt", "{}", "# md", now)));

        mockMvc.perform(get("/api/reporting/archive/run-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reportId").value("run-1"))
                .andExpect(jsonPath("$.markdownContent").value("# md"));
    }

    @Test
    void configurationReturnsReportingSettings() throws Exception {
        when(reportingService.getConfigurationView())
                .thenReturn(new ReportingService.ReportingConfigurationView(
                        true,
                        true,
                        true,
                        true,
                        3,
                        "Subject {date}",
                        "Bandar <bandar@example.com>",
                        List.of("a@gmail.com"),
                        List.of("Until tomorrow."),
                        new ReportingAttachmentProperties(),
                        true));

        mockMvc.perform(get("/api/reporting/dev/configuration"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enabled").value(true))
                .andExpect(jsonPath("$.recipients[0]").value("a@gmail.com"));
    }

    private static MemoryConsolidationReport sampleReport() {
        Instant now = Instant.parse("2026-06-01T06:00:00Z");
        return new MemoryConsolidationReport(
                "run-1", now, now, 10, 1, 1, 0, 0, 1, 0, 1, "Summary", "txt", "{}", "", "SKIPPED", "");
    }
}
