package com.chugalkhorbandar.adapters.api.controller;

import com.chugalkhorbandar.adapters.api.dto.StoryDetailsDto;
import com.chugalkhorbandar.adapters.api.dto.StorySummaryDto;
import com.chugalkhorbandar.adapters.api.mapper.WorldQueryDtoMapper;
import com.chugalkhorbandar.application.query.StoryQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stories")
@Tag(name = "Stories", description = "Read-only story queries")
public class StoryController {

    private final StoryQueryService storyQueryService;
    private final WorldQueryDtoMapper mapper;

    public StoryController(StoryQueryService storyQueryService, WorldQueryDtoMapper mapper) {
        this.storyQueryService = storyQueryService;
        this.mapper = mapper;
    }

    @GetMapping
    @Operation(summary = "List story summaries")
    public List<StorySummaryDto> list() {
        return storyQueryService.findAll().stream().map(mapper::toSummaryDto).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get detailed story view")
    public StoryDetailsDto getById(@PathVariable String id) {
        return mapper.toDetailsDto(storyQueryService.findDetailsById(id));
    }
}
