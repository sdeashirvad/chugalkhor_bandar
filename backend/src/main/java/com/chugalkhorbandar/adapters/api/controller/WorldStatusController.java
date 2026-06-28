package com.chugalkhorbandar.adapters.api.controller;

import com.chugalkhorbandar.adapters.api.dto.WorldStatusDto;
import com.chugalkhorbandar.adapters.api.mapper.WorldQueryDtoMapper;
import com.chugalkhorbandar.application.query.WorldStatusQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/world")
@Tag(name = "World", description = "Runtime world status")
public class WorldStatusController {

    private final WorldStatusQueryService worldStatusQueryService;
    private final WorldQueryDtoMapper mapper;

    public WorldStatusController(WorldStatusQueryService worldStatusQueryService, WorldQueryDtoMapper mapper) {
        this.worldStatusQueryService = worldStatusQueryService;
        this.mapper = mapper;
    }

    @GetMapping("/status")
    @Operation(summary = "Get runtime world status and aggregate counts")
    public WorldStatusDto status() {
        return mapper.toDto(worldStatusQueryService.getStatus());
    }
}
