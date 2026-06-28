package com.chugalkhorbandar.adapters.api.controller;

import com.chugalkhorbandar.adapters.api.dto.RelationshipDetailsDto;
import com.chugalkhorbandar.adapters.api.dto.RelationshipSummaryDto;
import com.chugalkhorbandar.adapters.api.mapper.WorldQueryDtoMapper;
import com.chugalkhorbandar.application.query.RelationshipQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/relationships")
@Tag(name = "Relationships", description = "Read-only relationship queries")
public class RelationshipController {

    private final RelationshipQueryService relationshipQueryService;
    private final WorldQueryDtoMapper mapper;

    public RelationshipController(RelationshipQueryService relationshipQueryService, WorldQueryDtoMapper mapper) {
        this.relationshipQueryService = relationshipQueryService;
        this.mapper = mapper;
    }

    @GetMapping
    @Operation(summary = "List relationship summaries")
    public List<RelationshipSummaryDto> list() {
        return relationshipQueryService.findAll().stream()
                .map(relationship -> new RelationshipSummaryDto(
                        relationship.id(),
                        relationship.title(),
                        relationship.sections().get("relationshipType"),
                        relationship.sections().get("relationshipStatus"),
                        null))
                .toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get detailed relationship view")
    public RelationshipDetailsDto getById(@PathVariable String id) {
        return mapper.toDetailsDto(relationshipQueryService.findDetailsById(id));
    }
}
