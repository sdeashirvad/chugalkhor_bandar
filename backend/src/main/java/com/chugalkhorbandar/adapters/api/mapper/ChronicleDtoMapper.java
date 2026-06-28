package com.chugalkhorbandar.adapters.api.mapper;

import com.chugalkhorbandar.adapters.api.dto.ChronicleProvenanceLinkResponseDto;
import com.chugalkhorbandar.adapters.api.dto.ChronicleProvenanceResponseDto;
import com.chugalkhorbandar.adapters.api.dto.ChronicleResponseDto;
import com.chugalkhorbandar.adapters.api.dto.ChronicleWriteExecutionResponseDto;
import com.chugalkhorbandar.adapters.api.dto.ChronicleWriteTraceEntryResponseDto;
import com.chugalkhorbandar.application.chronicle.Chronicle;
import com.chugalkhorbandar.application.chronicle.ChronicleProvenance;
import com.chugalkhorbandar.application.chronicle.ChronicleProvenanceLink;
import com.chugalkhorbandar.application.chronicle.ChronicleWriteResult;
import com.chugalkhorbandar.application.chronicle.ChronicleWriteTraceEntry;

public final class ChronicleDtoMapper {

    private ChronicleDtoMapper() {}

    public static ChronicleResponseDto toDto(Chronicle chronicle) {
        return new ChronicleResponseDto(
                chronicle.id(),
                chronicle.title(),
                chronicle.category().name(),
                chronicle.visibility().name(),
                chronicle.confidence().name(),
                chronicle.ownerCharacterId(),
                chronicle.summary(),
                chronicle.body(),
                chronicle.createdAt(),
                chronicle.chronicleDate(),
                chronicle.metadata(),
                toDto(chronicle.provenance()),
                chronicle.version());
    }

    public static ChronicleProvenanceResponseDto toDto(ChronicleProvenance provenance) {
        return new ChronicleProvenanceResponseDto(
                provenance.conversationId(),
                provenance.artifactIds(),
                provenance.observationIds(),
                provenance.inboxItemIds(),
                provenance.consolidationRunId(),
                provenance.candidateId(),
                provenance.chronicleId(),
                provenance.chain().stream().map(ChronicleDtoMapper::toDto).toList(),
                provenance.metadata());
    }

    public static ChronicleProvenanceLinkResponseDto toDto(ChronicleProvenanceLink link) {
        return new ChronicleProvenanceLinkResponseDto(link.stage(), link.entityId(), link.label());
    }

    public static ChronicleWriteExecutionResponseDto toDto(ChronicleWriteResult result) {
        return new ChronicleWriteExecutionResponseDto(
                result.runId(),
                result.startedAt(),
                result.completedAt(),
                result.durationMs(),
                result.candidatesProcessed(),
                result.chroniclesWritten(),
                result.skipped(),
                result.chronicles().stream().map(ChronicleDtoMapper::toDto).toList(),
                result.trace().stream().map(ChronicleDtoMapper::toTraceDto).toList());
    }

    public static ChronicleWriteTraceEntryResponseDto toTraceDto(ChronicleWriteTraceEntry entry) {
        return new ChronicleWriteTraceEntryResponseDto(entry.stage(), entry.rule(), entry.reason());
    }
}
