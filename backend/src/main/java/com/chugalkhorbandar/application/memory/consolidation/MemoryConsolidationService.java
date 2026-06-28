package com.chugalkhorbandar.application.memory.consolidation;

import com.chugalkhorbandar.application.artifacts.ConversationArtifact;
import com.chugalkhorbandar.application.artifacts.ConversationArtifactStatus;
import com.chugalkhorbandar.application.artifacts.ConversationArtifactType;
import com.chugalkhorbandar.application.reporting.ReportingDeliverySummary;
import com.chugalkhorbandar.application.reporting.ReportingService;
import com.chugalkhorbandar.application.memory.inbox.MemoryInboxItem;
import com.chugalkhorbandar.application.memory.inbox.MemoryInboxStatus;
import com.chugalkhorbandar.application.query.WorldStatusQueryService;
import com.chugalkhorbandar.domain.artifacts.ports.ConversationArtifactRepository;
import com.chugalkhorbandar.domain.memory.consolidation.ports.LongTermMemoryCandidateRepository;
import com.chugalkhorbandar.domain.memory.consolidation.ports.MemoryConsolidationReportRepository;
import com.chugalkhorbandar.domain.memory.inbox.ports.MemoryInboxRepository;
import com.chugalkhorbandar.domain.notification.ports.NotificationRepository;
import com.chugalkhorbandar.domain.world.ports.WorldRepositoryProvider;
import com.chugalkhorbandar.domain.world.ports.query.CharacterQuery;
import com.chugalkhorbandar.domain.world.runtime.RuntimeCharacter;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MemoryConsolidationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MemoryConsolidationService.class);

    private final MemoryConsolidationProperties properties;
    private final MemoryConsolidationEngine engine;
    private final MemoryConsolidationReportGenerator reportGenerator;
    private final MemoryConsolidationReflectionService reflectionService;
    private final ReportingService reportingService;
    private final MemoryInboxRepository inboxRepository;
    private final LongTermMemoryCandidateRepository candidateRepository;
    private final MemoryConsolidationReportRepository reportRepository;
    private final MemoryConsolidationGenerationStore generationStore;
    private final ConversationArtifactRepository artifactRepository;
    private final NotificationRepository notificationRepository;
    private final WorldRepositoryProvider worldRepositoryProvider;
    private final WorldStatusQueryService worldStatusQueryService;

    public MemoryConsolidationService(
            MemoryConsolidationProperties properties,
            MemoryConsolidationEngine engine,
            MemoryConsolidationReportGenerator reportGenerator,
            MemoryConsolidationReflectionService reflectionService,
            ReportingService reportingService,
            MemoryInboxRepository inboxRepository,
            LongTermMemoryCandidateRepository candidateRepository,
            MemoryConsolidationReportRepository reportRepository,
            MemoryConsolidationGenerationStore generationStore,
            ConversationArtifactRepository artifactRepository,
            NotificationRepository notificationRepository,
            WorldRepositoryProvider worldRepositoryProvider,
            WorldStatusQueryService worldStatusQueryService) {
        this.properties = properties;
        this.engine = engine;
        this.reportGenerator = reportGenerator;
        this.reflectionService = reflectionService;
        this.reportingService = reportingService;
        this.inboxRepository = inboxRepository;
        this.candidateRepository = candidateRepository;
        this.reportRepository = reportRepository;
        this.generationStore = generationStore;
        this.artifactRepository = artifactRepository;
        this.notificationRepository = notificationRepository;
        this.worldRepositoryProvider = worldRepositoryProvider;
        this.worldStatusQueryService = worldStatusQueryService;
    }

    public MemoryConsolidationReport runConsolidation() {
        if (!properties.isEnabled()) {
            throw new IllegalStateException("Memory consolidation is disabled");
        }
        String runId = UUID.randomUUID().toString();
        Instant startedAt = Instant.now();
        LocalDate currentDate = startedAt.atZone(ZoneOffset.UTC).toLocalDate();

        List<MemoryInboxItem> allInboxItems = inboxRepository.findAll();
        MemoryConsolidationDailyStats dailyStats = buildDailyStats(allInboxItems, currentDate);
        var worldStatus = worldStatusQueryService.getStatus();
        String runtimeWorldSummary = String.format(
                "status=%s, bootstrap=%s, characters=%d",
                worldStatus.status(), worldStatus.bootstrapVersion(), worldStatus.characters());

        MemoryConsolidationEngineInput input = new MemoryConsolidationEngineInput(
                allInboxItems, runtimeWorldSummary, currentDate, startedAt, dailyStats);
        MemoryConsolidationResult result = engine.consolidate(input);

        int promoted = 0;
        int discarded = 0;
        int archived = 0;
        for (MemoryConsolidationDecisionRecord decision : result.decisions()) {
            if (decision.decision() == MemoryConsolidationDecision.PROMOTE) {
                promoted += decision.inboxItems().size();
                for (MemoryInboxItem item : decision.inboxItems()) {
                    archiveInboxItem(item, MemoryInboxStatus.PROMOTED, startedAt);
                    archived++;
                }
            } else if (decision.decision() == MemoryConsolidationDecision.DISCARD) {
                discarded += decision.inboxItems().size();
                for (MemoryInboxItem item : decision.inboxItems()) {
                    archiveInboxItem(item, MemoryInboxStatus.DISCARDED, startedAt);
                    archived++;
                }
            }
        }

        List<LongTermMemoryCandidate> persistedCandidates = result.candidates().stream()
                .map(candidate -> candidateRepository.save(candidate.withRunId(runId)))
                .toList();

        String txtReport = reportGenerator.generateTxt(result.dailyStats(), result);
        String jsonReport;
        try {
            jsonReport = reportGenerator.generateJson(result.dailyStats(), result);
        } catch (Exception exception) {
            jsonReport = "{}";
        }
        String reflection = reflectionService.generateReflection(result, result.dailyStats());

        Instant completedAt = Instant.now();
        long durationMs = completedAt.toEpochMilli() - startedAt.toEpochMilli();
        int pending = countPendingInboxItems();

        MemoryConsolidationReport report = new MemoryConsolidationReport(
                runId,
                startedAt,
                completedAt,
                durationMs,
                result.dailyStats().inboxItems(),
                promoted,
                discarded,
                countExpired(allInboxItems, startedAt),
                archived,
                pending,
                persistedCandidates.size(),
                "Processed " + result.dailyStats().inboxItems() + " inbox items; promoted " + promoted + ", discarded "
                        + discarded + ", " + persistedCandidates.size() + " chronicle candidates",
                txtReport,
                jsonReport,
                reflection,
                "PENDING",
                "");

        ReportingDeliverySummary deliverySummary;
        if (properties.isEmailEnabled()) {
            deliverySummary = reportingService.processReport(report);
        } else {
            reportingService.archiveReport(report);
            deliverySummary = new ReportingDeliverySummary("SKIPPED", "", 0, 0);
        }

        MemoryConsolidationReport persistedReport = new MemoryConsolidationReport(
                report.runId(),
                report.startedAt(),
                report.completedAt(),
                report.durationMs(),
                report.processed(),
                report.promoted(),
                report.discarded(),
                report.expired(),
                report.archived(),
                report.pending(),
                report.candidateCount(),
                report.summary(),
                report.txtReport(),
                report.jsonReport(),
                report.reflection(),
                deliverySummary.status(),
                deliverySummary.error());

        reportRepository.save(persistedReport);
        generationStore.save(new MemoryConsolidationExecutionSnapshot(
                runId, startedAt, completedAt, result, persistedReport, persistedCandidates, reflection,
                deliverySummary.status(), deliverySummary.error()));
        LOGGER.info(
                "Memory consolidation run {} complete: promoted={}, discarded={}, candidates={}",
                runId,
                promoted,
                discarded,
                persistedCandidates.size());
        return persistedReport;
    }

    public Optional<MemoryConsolidationReport> getLatestReport() {
        return reportRepository.findLatest();
    }

    public List<MemoryConsolidationReport> getHistory() {
        return reportRepository.findHistoryOrderByStartedAtDesc();
    }

    public List<LongTermMemoryCandidate> getCandidatesForRun(String runId) {
        return candidateRepository.findByRunId(runId);
    }

    public List<LongTermMemoryCandidate> getAllCandidates() {
        return candidateRepository.findAllOrderByCreatedAtDesc();
    }

    public Optional<MemoryConsolidationExecutionSnapshot> getLatestExecution() {
        return generationStore.getLatest();
    }

    private MemoryConsolidationDailyStats buildDailyStats(
            List<MemoryInboxItem> inboxItems, LocalDate currentDate) {
        Set<String> conversationIds = new HashSet<>();
        for (MemoryInboxItem item : inboxItems) {
            String conversationId = item.metadata().get("conversationId");
            if (conversationId != null && !conversationId.isBlank()) {
                conversationIds.add(conversationId);
            }
        }
        int artifactCount = 0;
        int pendingPromises = 0;
        int unreadNotifications = 0;
        List<RuntimeCharacter> characters =
                worldRepositoryProvider.characters().findAll(CharacterQuery.all());
        for (RuntimeCharacter character : characters) {
            List<ConversationArtifact> artifacts = artifactRepository.findAllForCharacter(character.id());
            artifactCount += artifacts.size();
            pendingPromises += (int) artifacts.stream()
                    .filter(artifact -> artifact.type() == ConversationArtifactType.PROMISE)
                    .filter(artifact -> artifact.status() == ConversationArtifactStatus.NEW
                            || artifact.status() == ConversationArtifactStatus.ACTIVE)
                    .count();
            unreadNotifications += (int) notificationRepository.countUnreadByRecipientCharacterId(character.id());
        }
        return new MemoryConsolidationDailyStats(
                currentDate.toString(),
                conversationIds.size(),
                artifactCount,
                inboxItems.size(),
                0,
                0,
                0,
                pendingPromises,
                unreadNotifications);
    }

    private void archiveInboxItem(MemoryInboxItem item, MemoryInboxStatus intermediateStatus, Instant now) {
        MemoryInboxItem updated = inboxRepository.save(
                item.withStatus(intermediateStatus, now).withTraceAppend(intermediateStatus.name().toLowerCase()));
        inboxRepository.save(updated.withStatus(MemoryInboxStatus.ARCHIVED, now).withTraceAppend("archived"));
    }

    private int countPendingInboxItems() {
        return (int) inboxRepository.findAll().stream()
                .filter(item -> item.status() == MemoryInboxStatus.NEW
                        || item.status() == MemoryInboxStatus.REVIEWED)
                .count();
    }

    private static int countExpired(List<MemoryInboxItem> items, Instant now) {
        return (int) items.stream()
                .filter(item -> item.expiresAt().isBefore(now))
                .count();
    }
}
