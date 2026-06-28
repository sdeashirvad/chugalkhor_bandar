package com.chugalkhorbandar.adapters.api.dto;

public record LLMGenerateResponseDto(
        LLMProviderInfoDto provider, ProviderRequestDto request, ProviderResponseDto response) {}
