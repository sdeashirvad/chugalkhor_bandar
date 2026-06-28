package com.chugalkhorbandar.adapters.api.controller;

import com.chugalkhorbandar.adapters.api.dto.TerritoryDetailsDto;
import com.chugalkhorbandar.adapters.api.dto.TerritorySummaryDto;
import com.chugalkhorbandar.adapters.api.mapper.WorldQueryDtoMapper;
import com.chugalkhorbandar.application.query.TerritoryQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/territories")
@Tag(name = "Territories", description = "Read-only territory queries")
public class TerritoryController {

    private final TerritoryQueryService territoryQueryService;
    private final WorldQueryDtoMapper mapper;

    public TerritoryController(TerritoryQueryService territoryQueryService, WorldQueryDtoMapper mapper) {
        this.territoryQueryService = territoryQueryService;
        this.mapper = mapper;
    }

    @GetMapping
    @Operation(summary = "List territory summaries")
    public List<TerritorySummaryDto> list() {
        return territoryQueryService.findAll().stream().map(mapper::toSummaryDto).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get detailed territory view")
    public TerritoryDetailsDto getById(@PathVariable String id) {
        return mapper.toDetailsDto(territoryQueryService.findDetailsById(id));
    }
}
