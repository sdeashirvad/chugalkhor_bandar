package com.chugalkhorbandar.adapters.api.dto;

public record LLMProviderInfoDto(
        String type, String name, String description, boolean healthy, String model) {}
