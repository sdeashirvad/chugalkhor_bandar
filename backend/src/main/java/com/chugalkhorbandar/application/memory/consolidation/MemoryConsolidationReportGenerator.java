package com.chugalkhorbandar.application.memory.consolidation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class MemoryConsolidationReportGenerator {

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public String generateTxt(MemoryConsolidationDailyStats stats, MemoryConsolidationResult result) {
        StringBuilder builder = new StringBuilder();
        builder.append("Jungle Daily Report").append(System.lineSeparator());
        builder.append("Date: ").append(stats.date()).append(System.lineSeparator());
        builder.append("Conversations: ").append(stats.conversations()).append(System.lineSeparator());
        builder.append("Artifacts: ").append(stats.artifacts()).append(System.lineSeparator());
        builder.append("Inbox Items: ").append(stats.inboxItems()).append(System.lineSeparator());
        builder.append("Promoted: ").append(stats.promoted()).append(System.lineSeparator());
        builder.append("Discarded: ").append(stats.discarded()).append(System.lineSeparator());
        builder.append("Candidates: ").append(stats.candidates()).append(System.lineSeparator());
        builder.append("Pending Promises: ").append(stats.pendingPromises()).append(System.lineSeparator());
        builder.append("Unread Notifications: ").append(stats.unreadNotifications()).append(System.lineSeparator());
        builder.append(System.lineSeparator());
        builder.append("Summary").append(System.lineSeparator());
        builder.append(buildSummary(result)).append(System.lineSeparator());
        if (!result.candidates().isEmpty()) {
            builder.append(System.lineSeparator()).append("Chronicle Candidates").append(System.lineSeparator());
            for (LongTermMemoryCandidate candidate : result.candidates()) {
                builder.append("- ")
                        .append(candidate.summary())
                        .append(" (")
                        .append(candidate.importance())
                        .append(")")
                        .append(System.lineSeparator());
            }
        }
        return builder.toString();
    }

    public String generateJson(MemoryConsolidationDailyStats stats, MemoryConsolidationResult result)
            throws JsonProcessingException {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("title", "Jungle Daily Report");
        payload.put("date", stats.date());
        payload.put("conversations", stats.conversations());
        payload.put("artifacts", stats.artifacts());
        payload.put("inboxItems", stats.inboxItems());
        payload.put("promoted", stats.promoted());
        payload.put("discarded", stats.discarded());
        payload.put("candidates", stats.candidates());
        payload.put("pendingPromises", stats.pendingPromises());
        payload.put("unreadNotifications", stats.unreadNotifications());
        payload.put("summary", buildSummary(result));
        payload.put(
                "chronicleCandidates",
                result.candidates().stream()
                        .map(candidate -> Map.of(
                                "id", candidate.id(),
                                "ownerCharacterId", candidate.ownerCharacterId(),
                                "summary", candidate.summary(),
                                "importance", candidate.importance().name(),
                                "reason", candidate.reason(),
                                "sourceInboxItems", candidate.sourceInboxItems()))
                        .toList());
        payload.put(
                "decisions",
                result.decisions().stream()
                        .map(record -> Map.of(
                                "decision", record.decision().name(),
                                "reason", record.reason(),
                                "inboxItemIds",
                                record.inboxItems().stream().map(item -> item.id()).toList()))
                        .toList());
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(payload);
    }

    private static String buildSummary(MemoryConsolidationResult result) {
        long promoted = result.decisions().stream()
                .filter(record -> record.decision() == MemoryConsolidationDecision.PROMOTE)
                .count();
        long discarded = result.decisions().stream()
                .filter(record -> record.decision() == MemoryConsolidationDecision.DISCARD)
                .count();
        long pending = result.decisions().stream()
                .filter(record -> record.decision() == MemoryConsolidationDecision.PENDING)
                .count();
        return "Processed inbox review: " + promoted + " promoted, " + discarded + " discarded, " + pending
                + " pending, " + result.candidates().size() + " chronicle candidates created.";
    }
}
