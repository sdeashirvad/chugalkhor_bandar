package com.chugalkhorbandar.adapters.api.dto;

public record DroppedSectionDto(
        String sectionType, String title, int estimatedTokens, String reason) {}
