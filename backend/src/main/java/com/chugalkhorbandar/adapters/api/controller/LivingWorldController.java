package com.chugalkhorbandar.adapters.api.controller;

import com.chugalkhorbandar.adapters.api.dto.LivingWorldTickResponseDto;
import com.chugalkhorbandar.adapters.api.dto.WorldEventResponseDto;
import com.chugalkhorbandar.adapters.api.mapper.LivingWorldDtoMapper;
import com.chugalkhorbandar.application.world.living.LivingWorldProperties;
import com.chugalkhorbandar.application.world.living.LivingWorldService;
import com.chugalkhorbandar.application.world.living.WorldEventNotFoundException;
import com.chugalkhorbandar.application.world.living.WorldEventType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/world")
@Tag(name = "Living World", description = "Autonomous world heartbeat")
public class LivingWorldController {

    private final LivingWorldService livingWorldService;
    private final LivingWorldProperties properties;

    public LivingWorldController(LivingWorldService livingWorldService, LivingWorldProperties properties) {
        this.livingWorldService = livingWorldService;
        this.properties = properties;
    }

    @GetMapping("/events")
    @Operation(summary = "List world events")
    public List<WorldEventResponseDto> listEvents() {
        return livingWorldService.listEvents().stream().map(LivingWorldDtoMapper::toDto).toList();
    }

    @GetMapping("/events/{id}")
    @Operation(summary = "World event details")
    public WorldEventResponseDto getEvent(@PathVariable String id) {
        return LivingWorldDtoMapper.toDto(livingWorldService.getEvent(id));
    }

    @GetMapping("/events/type/{type}")
    @Operation(summary = "List world events by type")
    public List<WorldEventResponseDto> listByType(@PathVariable String type) {
        return livingWorldService
                .listEventsByType(WorldEventType.valueOf(type.toUpperCase()))
                .stream()
                .map(LivingWorldDtoMapper::toDto)
                .toList();
    }

    @GetMapping("/dev/latest-tick")
    @Operation(summary = "Latest world tick (developer)")
    public LivingWorldTickResponseDto latestTick() {
        return LivingWorldDtoMapper.toDto(
                livingWorldService.getLatestTick().orElseThrow(WorldEventNotFoundException::new));
    }

    @PostMapping("/dev/run-tick")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Run manual world tick (developer)")
    public LivingWorldTickResponseDto runTick() {
        if (!properties.isManualTickEnabled()) {
            throw new IllegalStateException("Manual world tick is disabled");
        }
        return LivingWorldDtoMapper.toDto(livingWorldService.runManualTick());
    }
}
