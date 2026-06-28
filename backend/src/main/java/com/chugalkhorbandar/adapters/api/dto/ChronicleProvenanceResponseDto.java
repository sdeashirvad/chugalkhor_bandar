package com.chugalkhorbandar.adapters.api.dto;

import java.util.List;
import java.util.Map;

public record ChronicleProvenanceResponseDto(
        String conversationId,
        List<String> artifactIds,
        List<String> observationIds,
        List<String> inboxItemIds,
        String consolidationRunId,
        String candidateId,
        String chronicleId,
        List<ChronicleProvenanceLinkResponseDto> chain,
        Map<String, String> metadata) {}
