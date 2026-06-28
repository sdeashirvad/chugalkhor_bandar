package com.chugalkhorbandar.application.memory.consolidation;

import com.chugalkhorbandar.application.memory.inbox.MemoryInboxImportance;
import com.chugalkhorbandar.application.memory.inbox.MemoryInboxItem;
import com.chugalkhorbandar.application.memory.inbox.MemoryInboxStatus;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class MemoryConsolidationEngine {

    private final MemoryConsolidationProperties properties;

    public MemoryConsolidationEngine(MemoryConsolidationProperties properties) {
        this.properties = properties;
    }

    public MemoryConsolidationResult consolidate(MemoryConsolidationEngineInput input) {
        List<MemoryConsolidationTraceEntry> trace = new ArrayList<>();
        List<MemoryConsolidationDecisionRecord> decisions = new ArrayList<>();
        List<LongTermMemoryCandidate> candidates = new ArrayList<>();
        MemoryConsolidationPromotionThresholds thresholds = properties.getPromotionThresholds();

        trace.add(new MemoryConsolidationTraceEntry("DISCOVER", "collect", "Scanning inbox items"));
        List<MemoryInboxItem> eligible = discoverEligible(input.inboxItems(), input.currentTime(), trace);

        trace.add(new MemoryConsolidationTraceEntry("FILTER", "status-filter", "Removing ineligible items"));
        List<MemoryInboxItem> filtered = filterEligible(eligible, input.currentTime(), trace);

        trace.add(new MemoryConsolidationTraceEntry("GROUP", "group-items", "Grouping related inbox items"));
        Map<String, List<MemoryInboxItem>> groups = groupItems(filtered, trace);

        trace.add(new MemoryConsolidationTraceEntry("DECIDE", "apply-rules", "Applying promotion rules"));
        Set<String> processedItemIds = new HashSet<>();
        for (List<MemoryInboxItem> group : groups.values()) {
            MemoryConsolidationDecision decision = decideGroup(group, thresholds, trace);
            if (decision == MemoryConsolidationDecision.PROMOTE) {
                LongTermMemoryCandidate candidate = buildCandidate(group, input.currentTime(), trace);
                candidates.add(candidate);
                decisions.add(new MemoryConsolidationDecisionRecord(
                        MemoryConsolidationDecision.PROMOTE, group, "Promotion rule matched", candidate));
            } else if (decision == MemoryConsolidationDecision.DISCARD) {
                decisions.add(new MemoryConsolidationDecisionRecord(
                        MemoryConsolidationDecision.DISCARD, group, "Discard rule matched", null));
            } else {
                decisions.add(new MemoryConsolidationDecisionRecord(
                        MemoryConsolidationDecision.PENDING, group, "Awaiting more evidence", null));
            }
            group.forEach(item -> processedItemIds.add(item.id()));
        }

        for (MemoryInboxItem item : filtered) {
            if (processedItemIds.contains(item.id())) {
                continue;
            }
            MemoryConsolidationDecision decision = decideSingle(item, thresholds, trace);
            List<MemoryInboxItem> singleton = List.of(item);
            if (decision == MemoryConsolidationDecision.PROMOTE) {
                LongTermMemoryCandidate candidate = buildCandidate(singleton, input.currentTime(), trace);
                candidates.add(candidate);
                decisions.add(new MemoryConsolidationDecisionRecord(
                        MemoryConsolidationDecision.PROMOTE, singleton, "Promotion rule matched", candidate));
            } else if (decision == MemoryConsolidationDecision.DISCARD) {
                decisions.add(new MemoryConsolidationDecisionRecord(
                        MemoryConsolidationDecision.DISCARD, singleton, "Discard rule matched", null));
            } else {
                decisions.add(new MemoryConsolidationDecisionRecord(
                        MemoryConsolidationDecision.PENDING, singleton, "Awaiting more evidence", null));
            }
        }

        trace.add(new MemoryConsolidationTraceEntry(
                "REPORT",
                "complete",
                "Processed " + filtered.size() + " items; promoted " + candidates.size()));

        MemoryConsolidationDailyStats stats = input.dailyStats() == null
                ? new MemoryConsolidationDailyStats(
                        input.currentDate().toString(),
                        0,
                        0,
                        filtered.size(),
                        countDecisions(decisions, MemoryConsolidationDecision.PROMOTE),
                        countDecisions(decisions, MemoryConsolidationDecision.DISCARD),
                        candidates.size(),
                        0,
                        0)
                : new MemoryConsolidationDailyStats(
                        input.dailyStats().date(),
                        input.dailyStats().conversations(),
                        input.dailyStats().artifacts(),
                        filtered.size(),
                        countDecisions(decisions, MemoryConsolidationDecision.PROMOTE),
                        countDecisions(decisions, MemoryConsolidationDecision.DISCARD),
                        candidates.size(),
                        input.dailyStats().pendingPromises(),
                        input.dailyStats().unreadNotifications());

        return new MemoryConsolidationResult(candidates, decisions, trace, stats);
    }

    private static List<MemoryInboxItem> discoverEligible(
            List<MemoryInboxItem> items, Instant now, List<MemoryConsolidationTraceEntry> trace) {
        List<MemoryInboxItem> eligible = items.stream()
                .filter(item -> item.status() == MemoryInboxStatus.NEW
                        || item.status() == MemoryInboxStatus.REVIEWED)
                .toList();
        trace.add(new MemoryConsolidationTraceEntry(
                "DISCOVER", "eligible-count", "Found " + eligible.size() + " eligible inbox items"));
        return eligible;
    }

    private static List<MemoryInboxItem> filterEligible(
            List<MemoryInboxItem> items, Instant now, List<MemoryConsolidationTraceEntry> trace) {
        List<MemoryInboxItem> filtered = new ArrayList<>();
        int skipped = 0;
        for (MemoryInboxItem item : items) {
            if (item.status() == MemoryInboxStatus.PROMOTED
                    || item.status() == MemoryInboxStatus.DISCARDED
                    || item.status() == MemoryInboxStatus.EXPIRED
                    || item.status() == MemoryInboxStatus.ARCHIVED) {
                skipped++;
                continue;
            }
            if (item.expiresAt().isBefore(now)) {
                skipped++;
                trace.add(new MemoryConsolidationTraceEntry(
                        "FILTER", "expired", "Skipping expired item " + item.id()));
                continue;
            }
            filtered.add(item);
        }
        trace.add(new MemoryConsolidationTraceEntry(
                "FILTER", "filtered-count", "Retained " + filtered.size() + "; skipped " + skipped));
        return filtered;
    }

    private static Map<String, List<MemoryInboxItem>> groupItems(
            List<MemoryInboxItem> items, List<MemoryConsolidationTraceEntry> trace) {
        Map<String, List<MemoryInboxItem>> groups = new HashMap<>();
        for (MemoryInboxItem item : items) {
            String key = groupKey(item);
            groups.computeIfAbsent(key, ignored -> new ArrayList<>()).add(item);
        }
        trace.add(new MemoryConsolidationTraceEntry(
                "GROUP", "group-count", "Formed " + groups.size() + " related groups"));
        return groups;
    }

    private static String groupKey(MemoryInboxItem item) {
        if (!item.artifactIds().isEmpty()) {
            return "artifact:" + item.ownerCharacterId() + ":" + item.artifactIds().getFirst();
        }
        if ("PROMOTE_TO_MEMORY".equals(item.type())) {
            return "recommendation:" + item.ownerCharacterId() + ":" + item.sourceId();
        }
        return "observation:" + item.ownerCharacterId() + ":" + item.type() + ":" + item.summary();
    }

    private MemoryConsolidationDecision decideGroup(
            List<MemoryInboxItem> group,
            MemoryConsolidationPromotionThresholds thresholds,
            List<MemoryConsolidationTraceEntry> trace) {
        MemoryInboxItem representative = group.getFirst();
        return decideForType(representative.type(), group.size(), representative.confidence(), thresholds, trace);
    }

    private MemoryConsolidationDecision decideSingle(
            MemoryInboxItem item,
            MemoryConsolidationPromotionThresholds thresholds,
            List<MemoryConsolidationTraceEntry> trace) {
        return decideForType(item.type(), 1, item.confidence(), thresholds, trace);
    }

    private MemoryConsolidationDecision decideForType(
            String type,
            int groupSize,
            double confidence,
            MemoryConsolidationPromotionThresholds thresholds,
            List<MemoryConsolidationTraceEntry> trace) {
        if ("UNKNOWN".equals(type)) {
            trace.add(new MemoryConsolidationTraceEntry("DECIDE", "unknown-discard", "UNKNOWN type discarded"));
            return MemoryConsolidationDecision.DISCARD;
        }
        if ("PROMISE".equals(type) || "PROMOTE_TO_MEMORY".equals(type)) {
            trace.add(new MemoryConsolidationTraceEntry("DECIDE", "always-promote", type + " always promoted"));
            return MemoryConsolidationDecision.PROMOTE;
        }
        if ("PREFERENCE".equals(type)) {
            if (groupSize >= thresholds.getPreferenceRepeatCount()) {
                trace.add(new MemoryConsolidationTraceEntry(
                        "DECIDE", "repeated-preference", "Repeated preference promoted"));
                return MemoryConsolidationDecision.PROMOTE;
            }
            if (confidence < thresholds.getMinimumConfidence()) {
                trace.add(new MemoryConsolidationTraceEntry(
                        "DECIDE", "low-confidence-discard", "Single low-confidence preference discarded"));
                return MemoryConsolidationDecision.DISCARD;
            }
            return MemoryConsolidationDecision.PENDING;
        }
        if ("STORY_SEED".equals(type)) {
            if (groupSize >= thresholds.getStorySeedRepeatCount()) {
                trace.add(new MemoryConsolidationTraceEntry(
                        "DECIDE", "repeated-story-seed", "Repeated story seed promoted"));
                return MemoryConsolidationDecision.PROMOTE;
            }
            if (confidence < thresholds.getMinimumConfidence()) {
                trace.add(new MemoryConsolidationTraceEntry(
                        "DECIDE", "low-confidence-discard", "Single low-confidence story seed discarded"));
                return MemoryConsolidationDecision.DISCARD;
            }
            return MemoryConsolidationDecision.PENDING;
        }
        if (confidence < thresholds.getMinimumConfidence()) {
            trace.add(new MemoryConsolidationTraceEntry(
                    "DECIDE", "low-confidence-discard", "Low-confidence observation discarded"));
            return MemoryConsolidationDecision.DISCARD;
        }
        return MemoryConsolidationDecision.PENDING;
    }

    private LongTermMemoryCandidate buildCandidate(
            List<MemoryInboxItem> items, Instant now, List<MemoryConsolidationTraceEntry> trace) {
        MemoryInboxItem primary = items.stream()
                .max(Comparator.comparingInt(MemoryConsolidationEngine::importanceRank)
                        .thenComparing(MemoryInboxItem::confidence))
                .orElse(items.getFirst());
        List<String> sourceIds = items.stream().map(MemoryInboxItem::id).toList();
        trace.add(new MemoryConsolidationTraceEntry(
                "PROMOTE", "create-candidate", "Creating candidate from " + sourceIds.size() + " inbox items"));
        return new LongTermMemoryCandidate(
                UUID.randomUUID().toString(),
                sourceIds,
                primary.ownerCharacterId(),
                primary.summary(),
                primary.importance(),
                "Consolidated from " + primary.type(),
                now,
                "",
                Map.of(
                        "type", primary.type(),
                        "source", primary.source().name(),
                        "confidence", String.valueOf(primary.confidence()),
                        "chronicleCandidate", "true"));
    }

    private static int importanceRank(MemoryInboxItem item) {
        return switch (item.importance()) {
            case VERY_HIGH -> 4;
            case HIGH -> 3;
            case MEDIUM -> 2;
            case LOW -> 1;
        };
    }

    private static int countDecisions(
            List<MemoryConsolidationDecisionRecord> decisions, MemoryConsolidationDecision target) {
        return (int) decisions.stream().filter(record -> record.decision() == target).count();
    }
}
