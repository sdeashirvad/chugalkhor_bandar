package com.chugalkhorbandar.application.reporting;

import com.chugalkhorbandar.application.email.ReportEmailProperties;
import com.chugalkhorbandar.application.memory.consolidation.MemoryConsolidationReport;
import com.chugalkhorbandar.domain.reporting.ports.DeliveryHistoryRepository;
import com.chugalkhorbandar.domain.reporting.ports.ReportArchiveRepository;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class ReportingService {

    private static final long RETRY_DELAY_2_MS = 60_000L;
    private static final long RETRY_DELAY_3_MS = 300_000L;

    private final ReportingProperties properties;
    private final ReportEmailProperties emailProperties;
    private final ReportingEngine reportingEngine;
    private final ResendReportDeliveryProvider deliveryProvider;
    private final ReportArchiveRepository archiveRepository;
    private final DeliveryHistoryRepository deliveryHistoryRepository;
    private final Map<String, Instant> retryNotBefore = new ConcurrentHashMap<>();

    public ReportingService(
            ReportingProperties properties,
            ReportEmailProperties emailProperties,
            ReportingEngine reportingEngine,
            ResendReportDeliveryProvider deliveryProvider,
            ReportArchiveRepository archiveRepository,
            DeliveryHistoryRepository deliveryHistoryRepository) {
        this.properties = properties;
        this.emailProperties = emailProperties;
        this.reportingEngine = reportingEngine;
        this.deliveryProvider = deliveryProvider;
        this.archiveRepository = archiveRepository;
        this.deliveryHistoryRepository = deliveryHistoryRepository;
    }

    public ReportArchive archiveReport(MemoryConsolidationReport report) {
        ReportArchive archive = reportingEngine.buildArchive(report);
        if (properties.isArchiveEnabled()) {
            archiveRepository.save(archive);
        }
        return archive;
    }

    public ReportingDeliverySummary processReport(MemoryConsolidationReport report) {
        if (!properties.isEnabled()) {
            return new ReportingDeliverySummary("SKIPPED", "Reporting disabled", 0, 0);
        }
        ReportArchive archive = archiveReport(report);
        if (!emailProperties.isEnabled() || !emailProperties.isConfigured()) {
            return new ReportingDeliverySummary("SKIPPED", "", 0, 0);
        }
        return deliverReport(report, archive, 1);
    }

    public ReportingDeliverySummary sendTestEmail(MemoryConsolidationReport report) {
        ReportArchive archive = archiveRepository.findByReportId(report.runId()).orElseGet(() -> {
            ReportArchive built = reportingEngine.buildArchive(report);
            if (properties.isArchiveEnabled()) {
                archiveRepository.save(built);
            }
            return built;
        });
        return deliverReport(report, archive, 1);
    }

    public ReportingDeliverySummary retryPendingDeliveries() {
        if (!properties.isEnabled() || !properties.isRetryEnabled()) {
            return new ReportingDeliverySummary("SKIPPED", "", 0, 0);
        }
        Instant now = Instant.now();
        int sent = 0;
        int failed = 0;
        for (DeliveryHistory failedEntry : deliveryHistoryRepository.findFailedEligibleForRetry(properties.getMaxRetries())) {
            String retryKey = failedEntry.reportId() + ":" + failedEntry.recipient();
            Instant notBefore = retryNotBefore.getOrDefault(retryKey, Instant.EPOCH);
            if (now.isBefore(notBefore)) {
                continue;
            }
            Optional<ReportArchive> archive = archiveRepository.findByReportId(failedEntry.reportId());
            if (archive.isEmpty()) {
                continue;
            }
            MemoryConsolidationReport synthetic = syntheticReport(failedEntry.reportId(), archive.get());
            ReportingDeliverySummary summary = deliverToRecipient(
                    synthetic, archive.get(), failedEntry.recipient(), failedEntry.attempt() + 1);
            sent += summary.recipientsSent();
            failed += summary.recipientsFailed();
        }
        return new ReportingDeliverySummary(failed == 0 ? "SENT" : "PARTIAL", "", sent, failed);
    }

    public Optional<ReportArchive> getArchive(String reportId) {
        return archiveRepository.findByReportId(reportId);
    }

    public List<ReportArchive> listArchives() {
        return archiveRepository.findAllOrderByCreatedAtDesc();
    }

    public List<DeliveryHistory> getDeliveryHistory() {
        return deliveryHistoryRepository.findAllOrderByCreatedAtDesc();
    }

    public DeliveryHistory getDeliveryHistoryEntry(String id) {
        return deliveryHistoryRepository.findById(id).orElseThrow(ReportingNotFoundException::new);
    }

    public String previewHtml(MemoryConsolidationReport report) {
        ensurePreviewEnabled();
        return archiveRepository.findByReportId(report.runId())
                .map(ReportArchive::htmlContent)
                .orElseGet(() -> reportingEngine.buildArchive(report).htmlContent());
    }

    public String previewTxt(MemoryConsolidationReport report) {
        ensurePreviewEnabled();
        return archiveRepository.findByReportId(report.runId())
                .map(ReportArchive::txtContent)
                .orElseGet(() -> reportingEngine.buildArchive(report).txtContent());
    }

    public String previewJson(MemoryConsolidationReport report) {
        ensurePreviewEnabled();
        return report.jsonReport();
    }

    public String previewMarkdown(MemoryConsolidationReport report) {
        ensurePreviewEnabled();
        return archiveRepository.findByReportId(report.runId())
                .map(ReportArchive::markdownContent)
                .orElseGet(() -> reportingEngine.buildArchive(report).markdownContent());
    }

    public ReportingConfigurationView getConfigurationView() {
        return new ReportingConfigurationView(
                properties.isEnabled(),
                properties.isArchiveEnabled(),
                properties.isRetryEnabled(),
                properties.isPreviewEnabled(),
                properties.getMaxRetries(),
                emailProperties.getSubjectTemplate() != null && !emailProperties.getSubjectTemplate().isBlank()
                        ? emailProperties.getSubjectTemplate()
                        : properties.getSubjectTemplate(),
                FriendlySenderParser.parse(emailProperties.getFrom()),
                RecipientParser.parse(emailProperties.getTo()),
                properties.getClosings(),
                properties.getAttachments(),
                emailProperties.isEnabled());
    }

    private ReportingDeliverySummary deliverReport(MemoryConsolidationReport report, ReportArchive archive, int attempt) {
        ReportingEmailPayload payload = reportingEngine.buildEmailPayload(report, archive);
        if (payload.recipients().isEmpty()) {
            return new ReportingDeliverySummary("SKIPPED", "No valid recipients configured", 0, 0);
        }
        int sent = 0;
        int failed = 0;
        StringBuilder errors = new StringBuilder();
        for (String recipient : payload.recipients()) {
            ReportingDeliverySummary result = deliverToRecipient(report, archive, recipient, attempt);
            sent += result.recipientsSent();
            failed += result.recipientsFailed();
            if (!result.error().isBlank()) {
                if (!errors.isEmpty()) {
                    errors.append("; ");
                }
                errors.append(result.error());
            }
        }
        String status = failed == 0 ? "SENT" : sent == 0 ? "FAILED" : "PARTIAL";
        return new ReportingDeliverySummary(status, errors.toString(), sent, failed);
    }

    private ReportingDeliverySummary deliverToRecipient(
            MemoryConsolidationReport report, ReportArchive archive, String recipient, int attempt) {
        ReportingEmailPayload payload = reportingEngine.buildEmailPayload(report, archive);
        ResendReportDeliveryProvider.DeliveryAttemptResult result =
                deliveryProvider.deliver(payload, recipient);
        DeliveryHistory entry = deliveryHistoryRepository.save(new DeliveryHistory(
                UUID.randomUUID().toString(),
                report.runId(),
                recipient,
                result.status(),
                "resend",
                result.providerMessageId(),
                attempt,
                result.latencyMs(),
                result.error(),
                Instant.now()));
        if ("FAILED".equals(result.status()) && properties.isRetryEnabled() && attempt < properties.getMaxRetries()) {
            long delayMs = attempt == 1 ? RETRY_DELAY_2_MS : RETRY_DELAY_3_MS;
            retryNotBefore.put(report.runId() + ":" + recipient, Instant.now().plusMillis(delayMs));
        }
        if ("SENT".equals(result.status())) {
            retryNotBefore.remove(report.runId() + ":" + recipient);
        }
        return new ReportingDeliverySummary(
                result.status(),
                result.error(),
                "SENT".equals(result.status()) ? 1 : 0,
                "FAILED".equals(result.status()) ? 1 : 0);
    }

    private void ensurePreviewEnabled() {
        if (!properties.isPreviewEnabled()) {
            throw new IllegalStateException("Report preview is disabled");
        }
    }

    private static MemoryConsolidationReport syntheticReport(String reportId, ReportArchive archive) {
        return new MemoryConsolidationReport(
                reportId,
                archive.createdAt(),
                archive.createdAt(),
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                "",
                archive.txtContent(),
                archive.jsonContent(),
                "",
                "SKIPPED",
                "");
    }

    public record ReportingConfigurationView(
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
}
