package com.chugalkhorbandar.application.chronicle;

import static org.assertj.core.api.Assertions.assertThat;

import com.chugalkhorbandar.application.artifacts.ConversationArtifact;
import com.chugalkhorbandar.application.artifacts.ConversationArtifactPriority;
import com.chugalkhorbandar.application.artifacts.ConversationArtifactStatus;
import com.chugalkhorbandar.application.artifacts.ConversationArtifactType;
import com.chugalkhorbandar.application.memory.consolidation.LongTermMemoryCandidate;
import com.chugalkhorbandar.application.memory.inbox.MemoryInboxImportance;
import com.chugalkhorbandar.application.memory.inbox.MemoryInboxItem;
import com.chugalkhorbandar.application.memory.inbox.MemoryInboxSource;
import com.chugalkhorbandar.application.memory.inbox.MemoryInboxStatus;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ChronicleWriterTest {

    private ChronicleWriter writer;

    @BeforeEach
    void setUp() {
        writer = new ChronicleWriter(new ChronicleWriterProperties(), new ChronicleProvenanceBuilder());
    }

    @Test
    void writesOneChroniclePerCandidateWithDeterministicId() {
        LongTermMemoryCandidate candidate = sampleCandidate();
        MemoryInboxItem inboxItem = sampleInboxItem();
        ConversationArtifact artifact = sampleArtifact();

        ChronicleWriteResult result = writer.write(
                "run-1",
                Instant.parse("2026-06-01T06:00:00Z"),
                List.of(candidate),
                LocalDate.parse("2026-06-01"),
                Map.of("character_alpha", "Hippu King"),
                Map.of(candidate.id(), List.of(inboxItem)),
                Map.of(candidate.id(), List.of(artifact)),
                Map.of());

        assertThat(result.chroniclesWritten()).isEqualTo(1);
        Chronicle chronicle = result.chronicles().get(0);
        assertThat(chronicle.id()).isEqualTo(ChronicleWriter.deterministicChronicleId(candidate.id(), 1));
        assertThat(chronicle.category()).isEqualTo(ChronicleCategory.PROMISE);
        assertThat(chronicle.visibility()).isEqualTo(ChronicleVisibility.PRIVATE);
        assertThat(chronicle.confidence()).isEqualTo(ChronicleConfidence.OFFICIAL);
        assertThat(chronicle.body()).contains("Bandar promised Hippu King");
        assertThat(chronicle.provenance().conversationId()).isEqualTo("conv-1");
        assertThat(chronicle.provenance().chain()).isNotEmpty();
        assertThat(chronicle.version()).isEqualTo(1);
    }

    @Test
    void skipsAlreadyWrittenCandidatesWhenVersioningDisabled() {
        ChronicleWriterProperties properties = new ChronicleWriterProperties();
        properties.setFutureVersioningEnabled(false);
        ChronicleWriter skipWriter = new ChronicleWriter(properties, new ChronicleProvenanceBuilder());

        LongTermMemoryCandidate candidate = sampleCandidate();
        ChronicleWriteResult first = skipWriter.write(
                "run-1",
                Instant.parse("2026-06-01T06:00:00Z"),
                List.of(candidate),
                LocalDate.parse("2026-06-01"),
                Map.of(),
                Map.of(candidate.id(), List.of(sampleInboxItem())),
                Map.of(candidate.id(), List.of(sampleArtifact())),
                Map.of());

        ChronicleWriteResult second = skipWriter.write(
                "run-2",
                Instant.parse("2026-06-01T07:00:00Z"),
                List.of(candidate),
                LocalDate.parse("2026-06-01"),
                Map.of(),
                Map.of(candidate.id(), List.of(sampleInboxItem())),
                Map.of(candidate.id(), List.of(sampleArtifact())),
                Map.of(candidate.id(), 1));

        assertThat(first.chroniclesWritten()).isEqualTo(1);
        assertThat(second.chroniclesWritten()).isEqualTo(0);
        assertThat(second.skipped()).isEqualTo(1);
    }

    private static LongTermMemoryCandidate sampleCandidate() {
        return new LongTermMemoryCandidate(
                "candidate-1",
                List.of("inbox-1"),
                "character_alpha",
                "one day he would tell the story of the Lost Crown",
                MemoryInboxImportance.HIGH,
                "Consolidated from PROMISE",
                Instant.parse("2026-06-01T00:00:00Z"),
                "consolidation-run-1",
                Map.of("type", "PROMISE", "source", "CONVERSATION_ARTIFACT", "confidence", "0.95", "chronicleCandidate", "true"));
    }

    private static MemoryInboxItem sampleInboxItem() {
        return new MemoryInboxItem(
                "inbox-1",
                "PROMISE",
                MemoryInboxSource.CONVERSATION_ARTIFACT,
                "artifact-1",
                "character_alpha",
                "one day he would tell the story of the Lost Crown",
                MemoryInboxImportance.HIGH,
                0.95,
                MemoryInboxStatus.ARCHIVED,
                Instant.parse("2026-06-01T00:00:00Z"),
                Instant.parse("2026-07-01T00:00:00Z"),
                Map.of("conversationId", "conv-1", "trigger", "promise-artifact"),
                List.of("created:promise-artifact"),
                "",
                List.of("artifact-1"));
    }

    private static ConversationArtifact sampleArtifact() {
        return new ConversationArtifact(
                "artifact-1",
                ConversationArtifactType.PROMISE,
                "character_alpha",
                "character_alpha",
                "character_bandar",
                "conv-1",
                "Lost Crown Promise",
                "one day he would tell the story of the Lost Crown",
                ConversationArtifactStatus.ACTIVE,
                ConversationArtifactPriority.HIGH,
                Instant.parse("2026-06-01T00:00:00Z"),
                Instant.parse("2026-06-01T00:00:00Z"),
                Instant.parse("2026-07-01T00:00:00Z"),
                Map.of("outcome", "PROMISE_MADE"),
                List.of("created:promise-made"));
    }
}
