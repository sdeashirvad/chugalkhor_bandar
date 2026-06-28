package com.chugalkhorbandar.adapters.api.controller;

import com.chugalkhorbandar.adapters.api.dto.LongTermMemoryCandidateResponseDto;
import com.chugalkhorbandar.adapters.api.dto.MemoryConsolidationExecutionResponseDto;
import com.chugalkhorbandar.adapters.api.dto.MemoryConsolidationReportResponseDto;
import com.chugalkhorbandar.adapters.api.mapper.MemoryConsolidationDtoMapper;
import com.chugalkhorbandar.application.memory.consolidation.MemoryConsolidationAsyncRunner;
import com.chugalkhorbandar.application.memory.consolidation.MemoryConsolidationNotFoundException;
import com.chugalkhorbandar.application.memory.consolidation.MemoryConsolidationProperties;
import com.chugalkhorbandar.application.memory.consolidation.MemoryConsolidationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/memory/consolidation")
@Tag(name = "Memory Consolidation", description = "Bandar's nightly memory consolidation")
public class MemoryConsolidationController {

    private final MemoryConsolidationService consolidationService;
    private final MemoryConsolidationAsyncRunner asyncRunner;
    private final MemoryConsolidationProperties properties;

    public MemoryConsolidationController(
            MemoryConsolidationService consolidationService,
            MemoryConsolidationAsyncRunner asyncRunner,
            MemoryConsolidationProperties properties) {
        this.consolidationService = consolidationService;
        this.asyncRunner = asyncRunner;
        this.properties = properties;
    }

    @GetMapping("/latest")
    @Operation(summary = "Get latest consolidation report")
    public MemoryConsolidationReportResponseDto latest() {
        return MemoryConsolidationDtoMapper.toDto(consolidationService
                .getLatestReport()
                .orElseThrow(MemoryConsolidationNotFoundException::new));
    }

    @GetMapping("/history")
    @Operation(summary = "Get consolidation report history")
    public List<MemoryConsolidationReportResponseDto> history() {
        return consolidationService.getHistory().stream()
                .map(MemoryConsolidationDtoMapper::toDto)
                .toList();
    }

    @PostMapping("/run")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Trigger manual consolidation run (developer)")
    public MemoryConsolidationReportResponseDto run() {
        if (!properties.isDeveloperManualRun()) {
            throw new IllegalStateException("Manual consolidation run is disabled");
        }
        return MemoryConsolidationDtoMapper.toDto(consolidationService.runConsolidation());
    }

    @PostMapping("/run/async")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Trigger async consolidation run (developer)")
    public void runAsync() {
        if (!properties.isDeveloperManualRun()) {
            throw new IllegalStateException("Manual consolidation run is disabled");
        }
        asyncRunner.runAsync();
    }

    @GetMapping("/dev/execution")
    @Operation(summary = "Latest consolidation execution trace (developer)")
    public MemoryConsolidationExecutionResponseDto latestExecution() {
        return MemoryConsolidationDtoMapper.toDto(consolidationService
                .getLatestExecution()
                .orElseThrow(MemoryConsolidationNotFoundException::new));
    }

    @GetMapping("/dev/candidates")
    @Operation(summary = "All long-term memory candidates (developer)")
    public List<LongTermMemoryCandidateResponseDto> allCandidates() {
        return consolidationService.getAllCandidates().stream()
                .map(MemoryConsolidationDtoMapper::toDto)
                .toList();
    }
}
