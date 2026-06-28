package com.chugalkhorbandar.adapters.api.controller;

import com.chugalkhorbandar.adapters.api.dto.OrganizationDetailsDto;
import com.chugalkhorbandar.adapters.api.dto.OrganizationSummaryDto;
import com.chugalkhorbandar.adapters.api.mapper.WorldQueryDtoMapper;
import com.chugalkhorbandar.application.query.OrganizationQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/organizations")
@Tag(name = "Organizations", description = "Read-only organization queries")
public class OrganizationController {

    private final OrganizationQueryService organizationQueryService;
    private final WorldQueryDtoMapper mapper;

    public OrganizationController(OrganizationQueryService organizationQueryService, WorldQueryDtoMapper mapper) {
        this.organizationQueryService = organizationQueryService;
        this.mapper = mapper;
    }

    @GetMapping
    @Operation(summary = "List organization summaries")
    public List<OrganizationSummaryDto> list() {
        return organizationQueryService.findAll().stream().map(mapper::toSummaryDto).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get detailed organization view")
    public OrganizationDetailsDto getById(@PathVariable String id) {
        return mapper.toDetailsDto(organizationQueryService.findDetailsById(id));
    }
}
