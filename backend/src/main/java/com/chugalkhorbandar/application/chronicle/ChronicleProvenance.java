package com.chugalkhorbandar.application.chronicle;

import java.util.List;
import java.util.Map;

public record ChronicleProvenance(
        String conversationId,
        List<String> artifactIds,
        List<String> observationIds,
        List<String> inboxItemIds,
        String consolidationRunId,
        String candidateId,
        String chronicleId,
        List<ChronicleProvenanceLink> chain,
        Map<String, String> metadata) {

    public ChronicleProvenance {
        artifactIds = List.copyOf(artifactIds == null ? List.of() : artifactIds);
        observationIds = List.copyOf(observationIds == null ? List.of() : observationIds);
        inboxItemIds = List.copyOf(inboxItemIds == null ? List.of() : inboxItemIds);
        chain = List.copyOf(chain == null ? List.of() : chain);
        metadata = Map.copyOf(metadata == null ? Map.of() : metadata);
        conversationId = conversationId == null ? "" : conversationId;
        consolidationRunId = consolidationRunId == null ? "" : consolidationRunId;
        candidateId = candidateId == null ? "" : candidateId;
        chronicleId = chronicleId == null ? "" : chronicleId;
    }
}
