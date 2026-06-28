package com.chugalkhorbandar.adapters.api.controller;

import com.chugalkhorbandar.adapters.api.dto.CharacterDetailsDto;
import com.chugalkhorbandar.adapters.api.dto.CharacterSummaryDto;
import com.chugalkhorbandar.adapters.api.mapper.WorldQueryDtoMapper;
import com.chugalkhorbandar.application.query.CharacterQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/characters")
@Tag(name = "Characters", description = "Read-only character queries")
public class CharacterController {

    private final CharacterQueryService characterQueryService;
    private final WorldQueryDtoMapper mapper;

    public CharacterController(CharacterQueryService characterQueryService, WorldQueryDtoMapper mapper) {
        this.characterQueryService = characterQueryService;
        this.mapper = mapper;
    }

    @GetMapping
    @Operation(summary = "List character summaries sorted by name")
    public List<CharacterSummaryDto> list(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String place) {
        if (title != null && !title.isBlank()) {
            return characterQueryService.findByTitle(title).stream()
                    .map(mapper::toSummaryDto)
                    .toList();
        }
        if (place != null && !place.isBlank()) {
            return characterQueryService.findByPlace(place).stream()
                    .map(mapper::toSummaryDto)
                    .toList();
        }
        return characterQueryService.findAll().stream().map(mapper::toSummaryDto).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get detailed character view")
    public CharacterDetailsDto getById(@PathVariable String id) {
        return mapper.toDetailsDto(characterQueryService.findDetailsById(id));
    }
}
