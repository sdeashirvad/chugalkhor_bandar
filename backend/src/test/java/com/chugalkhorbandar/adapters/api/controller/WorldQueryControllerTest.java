package com.chugalkhorbandar.adapters.api.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.chugalkhorbandar.adapters.api.ApiExceptionHandler;
import com.chugalkhorbandar.adapters.api.mapper.WorldQueryDtoMapper;
import com.chugalkhorbandar.application.query.CharacterQueryService;
import com.chugalkhorbandar.application.query.ResourceNotFoundException;
import com.chugalkhorbandar.application.query.StoryQueryService;
import com.chugalkhorbandar.application.query.TerritoryQueryService;
import com.chugalkhorbandar.application.query.WorldStatus;
import com.chugalkhorbandar.application.query.WorldStatusQueryService;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {
    WorldStatusController.class,
    CharacterController.class,
    StoryController.class,
    TerritoryController.class
})
@Import(ApiExceptionHandler.class)
class WorldQueryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WorldStatusQueryService worldStatusQueryService;

    @MockitoBean
    private CharacterQueryService characterQueryService;

    @MockitoBean
    private StoryQueryService storyQueryService;

    @MockitoBean
    private TerritoryQueryService territoryQueryService;

    @MockitoBean
    private WorldQueryDtoMapper mapper;

    @Test
    void worldStatusEndpointReturnsReadyStatus() throws Exception {
        when(worldStatusQueryService.getStatus())
                .thenReturn(new WorldStatus(
                        "READY",
                        "1.0",
                        Instant.parse("2026-06-27T00:00:00Z"),
                        Instant.parse("2026-06-27T12:00:00Z"),
                        "IN_MEMORY_H2",
                        13,
                        3,
                        1,
                        1,
                        1,
                        1,
                        5,
                        Map.of("Hippu", 10),
                        Map.of("Ancient History", 2)));
        when(mapper.toDto(org.mockito.ArgumentMatchers.any())).thenAnswer(invocation -> {
            WorldStatus status = invocation.getArgument(0);
            return new com.chugalkhorbandar.adapters.api.dto.WorldStatusDto(
                    status.status(),
                    status.bootstrapVersion(),
                    status.bootstrapTimestamp(),
                    status.runtimeStartedAt(),
                    status.persistenceProvider(),
                    status.characters(),
                    status.stories(),
                    status.territories(),
                    status.places(),
                    status.organizations(),
                    status.relationships(),
                    status.timelineEntries(),
                    status.charactersBySpecies(),
                    status.storiesByEra());
        });

        mockMvc.perform(get("/api/world/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("READY"))
                .andExpect(jsonPath("$.bootstrapVersion").value("1.0"))
                .andExpect(jsonPath("$.characters").value(13))
                .andExpect(jsonPath("$.persistenceProvider").value("IN_MEMORY_H2"));
    }

    @Test
    void unknownCharacterReturns404() throws Exception {
        when(characterQueryService.findDetailsById("missing"))
                .thenThrow(new ResourceNotFoundException("Character", "missing"));

        mockMvc.perform(get("/api/characters/missing"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Character not found: missing"));
    }

    @Test
    void unknownStoryReturns404() throws Exception {
        when(storyQueryService.findDetailsById("missing"))
                .thenThrow(new ResourceNotFoundException("Story", "missing"));

        mockMvc.perform(get("/api/stories/missing"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Story not found: missing"));
    }

    @Test
    void unknownTerritoryReturns404() throws Exception {
        when(territoryQueryService.findDetailsById("missing"))
                .thenThrow(new ResourceNotFoundException("Territory", "missing"));

        mockMvc.perform(get("/api/territories/missing"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Territory not found: missing"));
    }

    @Test
    void characterListEndpointReturnsSummaries() throws Exception {
        when(characterQueryService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/characters")).andExpect(status().isOk());
    }
}
