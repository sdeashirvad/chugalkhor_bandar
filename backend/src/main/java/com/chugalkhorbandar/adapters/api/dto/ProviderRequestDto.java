package com.chugalkhorbandar.adapters.api.dto;

import java.util.List;
import java.util.Map;

public record ProviderRequestDto(
        List<ProviderMessageDto> messages,
        Map<String, String> metadata,
        double temperature,
        int maxOutputTokens,
        String model) {}
