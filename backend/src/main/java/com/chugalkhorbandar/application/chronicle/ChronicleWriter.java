package com.chugalkhorbandar.application.chronicle;

import com.chugalkhorbandar.application.artifacts.ConversationArtifact;
import com.chugalkhorbandar.application.memory.consolidation.LongTermMemoryCandidate;
import com.chugalkhorbandar.application.memory.inbox.MemoryInboxItem;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class ChronicleWriter {

    private final ChronicleWriterProperties properties;
    private final ChronicleProvenanceBuilder provenanceBuilder;

    public ChronicleWriter(ChronicleWriterProperties properties, ChronicleProvenanceBuilder provenanceBuilder) {
        this.properties = properties;
        this.provenanceBuilder = provenanceBuilder;
    }

    public ChronicleWriteResult write(
            String runId,
            Instant startedAt,
            List<LongTermMemoryCandidate> candidates,
            LocalDate currentDate,
            Map<String, String> characterNames,
            Map<String, List<MemoryInboxItem>> inboxItemsByCandidate,
            Map<String, List<ConversationArtifact>> artifactsByCandidate,
            Map<String, Integer> existingVersionsByCandidate) {
        List<Chronicle> chronicles = new ArrayList<>();
        List<ChronicleWriteTraceEntry> trace = new ArrayList<>();
        int skipped = 0;

        for (LongTermMemoryCandidate candidate : candidates) {
            if (!isChronicleCandidate(candidate)) {
                skipped++;
                trace.add(new ChronicleWriteTraceEntry(
                        "SKIP", "not-chronicle-candidate", "Candidate metadata missing chronicleCandidate flag"));
                continue;
            }
            int version = existingVersionsByCandidate.getOrDefault(candidate.id(), 0) + 1;
            if (existingVersionsByCandidate.containsKey(candidate.id()) && !properties.isFutureVersioningEnabled()) {
                skipped++;
                trace.add(new ChronicleWriteTraceEntry(
                        "SKIP", "already-written", "Chronicle already exists for candidate " + candidate.id()));
                continue;
            }

            List<MemoryInboxItem> inboxItems =
                    inboxItemsByCandidate.getOrDefault(candidate.id(), List.of());
            List<ConversationArtifact> artifacts =
                    artifactsByCandidate.getOrDefault(candidate.id(), List.of());

            Chronicle chronicle = writeOne(candidate, currentDate, characterNames, inboxItems, artifacts, version);
            chronicles.add(chronicle);
            trace.add(new ChronicleWriteTraceEntry(
                    "WRITE",
                    ChronicleBodyTemplateRenderer.templateName(chronicle.category()),
                    "Wrote chronicle " + chronicle.id() + " from candidate " + candidate.id()));
        }

        Instant completedAt = Instant.now();
        return new ChronicleWriteResult(
                runId,
                startedAt,
                completedAt,
                completedAt.toEpochMilli() - startedAt.toEpochMilli(),
                candidates.size(),
                chronicles.size(),
                skipped,
                chronicles,
                trace);
    }

    public Chronicle writeOne(
            LongTermMemoryCandidate candidate,
            LocalDate currentDate,
            Map<String, String> characterNames,
            List<MemoryInboxItem> inboxItems,
            List<ConversationArtifact> artifacts,
            int version) {
        ChronicleCategory category = ChronicleCategoryMapper.fromMetadata(candidate.metadata());
        ChronicleVisibility visibility = ChronicleVisibilityMapper.resolve(category, properties);
        double inboxConfidence = parseConfidence(candidate.metadata().get("confidence"));
        ChronicleConfidence confidence = ChronicleConfidenceMapper.withDefault(inboxConfidence, properties);

        String ownerName = characterNames.getOrDefault(candidate.ownerCharacterId(), candidate.ownerCharacterId());
        String recipientName = resolveRecipientName(artifacts, characterNames);

        String chronicleId = deterministicChronicleId(candidate.id(), version);
        ChronicleProvenance provenance =
                provenanceBuilder.build(candidate, chronicleId, inboxItems, artifacts);

        String body = ChronicleBodyTemplateRenderer.render(category, ownerName, recipientName, candidate.summary());
        String title = ChronicleBodyTemplateRenderer.renderTitle(category, candidate.summary());

        Map<String, String> metadata = new LinkedHashMap<>(candidate.metadata());
        metadata.put("candidateId", candidate.id());
        metadata.put("consolidationRunId", candidate.runId());
        metadata.put("template", ChronicleBodyTemplateRenderer.templateName(category));
        metadata.put("importance", candidate.importance().name());

        Instant createdAt = Instant.now();
        LocalDate chronicleDate = currentDate == null
                ? createdAt.atZone(ZoneOffset.UTC).toLocalDate()
                : currentDate;

        return new Chronicle(
                chronicleId,
                title,
                category,
                visibility,
                confidence,
                candidate.ownerCharacterId(),
                candidate.summary(),
                body,
                createdAt,
                chronicleDate,
                metadata,
                provenance,
                version);
    }

    public static String deterministicChronicleId(String candidateId, int version) {
        return "chron-" + candidateId + "-v" + Math.max(1, version);
    }

    private static boolean isChronicleCandidate(LongTermMemoryCandidate candidate) {
        return "true".equalsIgnoreCase(candidate.metadata().getOrDefault("chronicleCandidate", "true"));
    }

    private static double parseConfidence(String raw) {
        if (raw == null || raw.isBlank()) {
            return 0.5;
        }
        try {
            return Double.parseDouble(raw);
        } catch (NumberFormatException exception) {
            return 0.5;
        }
    }

    private static String resolveRecipientName(
            List<ConversationArtifact> artifacts, Map<String, String> characterNames) {
        Optional<String> recipientId = artifacts.stream()
                .map(ConversationArtifact::recipientCharacterId)
                .filter(id -> id != null && !id.isBlank())
                .findFirst();
        return recipientId.map(id -> characterNames.getOrDefault(id, id)).orElse("a companion");
    }
}
