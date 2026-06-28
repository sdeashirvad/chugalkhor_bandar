package com.chugalkhorbandar.adapters.api.mapper;

import com.chugalkhorbandar.adapters.api.dto.LongTermMemoryCandidateResponseDto;
import com.chugalkhorbandar.adapters.api.dto.MemoryConsolidationDecisionResponseDto;
import com.chugalkhorbandar.adapters.api.dto.MemoryConsolidationExecutionResponseDto;
import com.chugalkhorbandar.adapters.api.dto.MemoryConsolidationReportResponseDto;
import com.chugalkhorbandar.application.memory.consolidation.LongTermMemoryCandidate;
import com.chugalkhorbandar.application.memory.consolidation.MemoryConsolidationDecisionRecord;
import com.chugalkhorbandar.application.memory.consolidation.MemoryConsolidationExecutionSnapshot;
import com.chugalkhorbandar.application.memory.consolidation.MemoryConsolidationReport;

public final class MemoryConsolidationDtoMapper {

    private MemoryConsolidationDtoMapper() {}

    public static MemoryConsolidationReportResponseDto toDto(MemoryConsolidationReport report) {
        return new MemoryConsolidationReportResponseDto(
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
                report.emailStatus(),
                report.emailError());
    }

    public static LongTermMemoryCandidateResponseDto toDto(LongTermMemoryCandidate candidate) {
        return new LongTermMemoryCandidateResponseDto(
                candidate.id(),
                candidate.sourceInboxItems(),
                candidate.ownerCharacterId(),
                candidate.summary(),
                candidate.importance(),
                candidate.reason(),
                candidate.createdAt(),
                candidate.runId(),
                candidate.metadata());
    }

    public static MemoryConsolidationExecutionResponseDto toDto(MemoryConsolidationExecutionSnapshot snapshot) {
        return new MemoryConsolidationExecutionResponseDto(
                snapshot.runId(),
                snapshot.startedAt(),
                snapshot.completedAt(),
                toDto(snapshot.report()),
                snapshot.candidates().stream().map(MemoryConsolidationDtoMapper::toDto).toList(),
                snapshot.result().trace(),
                snapshot.result().decisions().stream()
                        .map(MemoryConsolidationDtoMapper::toDto)
                        .toList(),
                snapshot.reflection(),
                snapshot.emailStatus(),
                snapshot.emailError());
    }

    private static MemoryConsolidationDecisionResponseDto toDto(MemoryConsolidationDecisionRecord record) {
        return new MemoryConsolidationDecisionResponseDto(
                record.decision(),
                record.reason(),
                record.inboxItems().stream().map(item -> item.id()).toList());
    }
}
