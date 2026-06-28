package com.chugalkhorbandar.adapters.persistence;

import com.chugalkhorbandar.domain.world.commands.TimelineEntry;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class JsonSerialization {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final TypeReference<Map<String, String>> MAP_TYPE = new TypeReference<>() {};
    private static final TypeReference<List<TimelineEntry>> TIMELINE_TYPE = new TypeReference<>() {};

    private JsonSerialization() {}

    public static String toJson(Map<String, String> map) {
        try {
            return MAPPER.writeValueAsString(map == null ? Map.of() : map);
        } catch (JsonProcessingException exception) {
            throw new PersistenceException("Failed to serialize map", exception);
        }
    }

    public static Map<String, String> toMap(String json) {
        if (json == null || json.isBlank()) {
            return Map.of();
        }
        try {
            return Map.copyOf(MAPPER.readValue(json, MAP_TYPE));
        } catch (JsonProcessingException exception) {
            throw new PersistenceException("Failed to deserialize map", exception);
        }
    }

    public static String toTimelineJson(List<TimelineEntry> entries) {
        try {
            return MAPPER.writeValueAsString(entries == null ? List.of() : entries);
        } catch (JsonProcessingException exception) {
            throw new PersistenceException("Failed to serialize timeline entries", exception);
        }
    }

    public static List<TimelineEntry> toTimelineEntries(String json) {
        if (json == null || json.isBlank()) {
            return List.of();
        }
        try {
            return List.copyOf(MAPPER.readValue(json, TIMELINE_TYPE));
        } catch (JsonProcessingException exception) {
            throw new PersistenceException("Failed to deserialize timeline entries", exception);
        }
    }

    public static Map<String, String> mutableMap(Map<String, String> source) {
        return new LinkedHashMap<>(source == null ? Map.of() : source);
    }
}
