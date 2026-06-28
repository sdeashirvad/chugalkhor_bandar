package com.chugalkhorbandar.adapters.api.dto;

import java.util.List;
import java.util.Map;

public record StoryDetailsDto(
        String id,
        String title,
        String summary,
        String era,
        List<EntityReferenceDto> participants,
        List<EntityReferenceDto> places,
        Map<String, String> sections,
        Map<String, String> linkedStories) {}
