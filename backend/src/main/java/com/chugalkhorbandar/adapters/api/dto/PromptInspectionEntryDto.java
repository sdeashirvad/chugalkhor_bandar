package com.chugalkhorbandar.adapters.api.dto;

public record PromptInspectionEntryDto(
        String sectionType, String title, int priority, boolean required, int estimatedTokens) {}
