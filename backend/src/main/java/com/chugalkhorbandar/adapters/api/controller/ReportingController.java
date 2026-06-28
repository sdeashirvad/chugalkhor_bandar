package com.chugalkhorbandar.adapters.api.controller;

import com.chugalkhorbandar.adapters.api.dto.DeliveryHistoryResponseDto;
import com.chugalkhorbandar.adapters.api.dto.ReportArchiveResponseDto;
import com.chugalkhorbandar.adapters.api.dto.ReportingConfigurationResponseDto;
import com.chugalkhorbandar.adapters.api.mapper.ReportingDtoMapper;
import com.chugalkhorbandar.application.memory.consolidation.MemoryConsolidationNotFoundException;
import com.chugalkhorbandar.application.memory.consolidation.MemoryConsolidationReport;
import com.chugalkhorbandar.application.memory.consolidation.MemoryConsolidationService;
import com.chugalkhorbandar.application.reporting.DeliveryHistory;
import com.chugalkhorbandar.application.reporting.ReportArchive;
import com.chugalkhorbandar.application.reporting.ReportingDeliverySummary;
import com.chugalkhorbandar.application.reporting.ReportingNotFoundException;
import com.chugalkhorbandar.application.reporting.ReportingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reporting")
@Tag(name = "Reporting", description = "Report delivery, archive, and preview")
public class ReportingController {

    private final ReportingService reportingService;
    private final MemoryConsolidationService consolidationService;

    public ReportingController(ReportingService reportingService, MemoryConsolidationService consolidationService) {
        this.reportingService = reportingService;
        this.consolidationService = consolidationService;
    }

    @GetMapping("/preview/html")
    @Operation(summary = "Preview HTML report (developer)")
    public ResponseEntity<String> previewHtml() {
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(reportingService.previewHtml(latestReport()));
    }

    @GetMapping("/preview/txt")
    @Operation(summary = "Preview TXT report (developer)")
    public ResponseEntity<String> previewTxt() {
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(reportingService.previewTxt(latestReport()));
    }

    @GetMapping("/preview/json")
    @Operation(summary = "Preview JSON report (developer)")
    public ResponseEntity<String> previewJson() {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(reportingService.previewJson(latestReport()));
    }

    @GetMapping("/preview/md")
    @Operation(summary = "Preview Markdown report (developer)")
    public ResponseEntity<String> previewMarkdown() {
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(reportingService.previewMarkdown(latestReport()));
    }

    @PostMapping("/send-test")
    @Operation(summary = "Send test email using latest consolidation report (developer)")
    public Map<String, Object> sendTestEmail() {
        ReportingDeliverySummary summary = reportingService.sendTestEmail(latestReport());
        return Map.of(
                "status", summary.status(),
                "error", summary.error(),
                "recipientsSent", summary.recipientsSent(),
                "recipientsFailed", summary.recipientsFailed());
    }

    @GetMapping("/history")
    @Operation(summary = "Delivery history (developer)")
    public List<DeliveryHistoryResponseDto> history() {
        return reportingService.getDeliveryHistory().stream().map(ReportingDtoMapper::toDto).toList();
    }

    @GetMapping("/history/{id}")
    @Operation(summary = "Delivery history entry (developer)")
    public DeliveryHistoryResponseDto historyEntry(@PathVariable String id) {
        return ReportingDtoMapper.toDto(reportingService.getDeliveryHistoryEntry(id));
    }

    @GetMapping("/archive")
    @Operation(summary = "Report archive list (developer)")
    public List<ReportArchiveResponseDto> archives() {
        return reportingService.listArchives().stream().map(ReportingDtoMapper::toDto).toList();
    }

    @GetMapping("/archive/{reportId}")
    @Operation(summary = "Report archive entry (developer)")
    public ReportArchiveResponseDto archive(@PathVariable String reportId) {
        ReportArchive archive = reportingService.getArchive(reportId).orElseThrow(ReportingNotFoundException::new);
        return ReportingDtoMapper.toDto(archive);
    }

    @GetMapping("/dev/configuration")
    @Operation(summary = "Current reporting configuration (developer)")
    public ReportingConfigurationResponseDto configuration() {
        return ReportingDtoMapper.toDto(reportingService.getConfigurationView());
    }

    private MemoryConsolidationReport latestReport() {
        return consolidationService.getLatestReport().orElseThrow(MemoryConsolidationNotFoundException::new);
    }
}
