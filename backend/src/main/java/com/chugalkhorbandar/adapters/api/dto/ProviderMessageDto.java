package com.chugalkhorbandar.adapters.api.dto;

import java.util.Map;

public record ProviderMessageDto(String role, String content, String sectionType, Map<String, String> metadata) {}
