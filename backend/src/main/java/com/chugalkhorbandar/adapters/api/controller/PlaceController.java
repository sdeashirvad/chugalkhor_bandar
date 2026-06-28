package com.chugalkhorbandar.adapters.api.controller;

import com.chugalkhorbandar.adapters.api.dto.PlaceDetailsDto;
import com.chugalkhorbandar.adapters.api.dto.PlaceSummaryDto;
import com.chugalkhorbandar.adapters.api.mapper.WorldQueryDtoMapper;
import com.chugalkhorbandar.application.query.PlaceQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/places")
@Tag(name = "Places", description = "Read-only place queries")
public class PlaceController {

    private final PlaceQueryService placeQueryService;
    private final WorldQueryDtoMapper mapper;

    public PlaceController(PlaceQueryService placeQueryService, WorldQueryDtoMapper mapper) {
        this.placeQueryService = placeQueryService;
        this.mapper = mapper;
    }

    @GetMapping
    @Operation(summary = "List place summaries")
    public List<PlaceSummaryDto> list() {
        return placeQueryService.findAll().stream().map(mapper::toSummaryDto).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get detailed place view")
    public PlaceDetailsDto getById(@PathVariable String id) {
        return mapper.toDetailsDto(placeQueryService.findDetailsById(id));
    }
}
