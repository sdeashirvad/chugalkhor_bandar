package com.chugalkhorbandar.adapters.api.dto;

public record SectionBudgetDto(
        String sectionType,
        int maxTokens,
        int minimumTokens,
        int priority,
        boolean required) {}
