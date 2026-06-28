package com.chugalkhorbandar.adapters.api.mapper;

import com.chugalkhorbandar.adapters.api.dto.LLMGenerateResponseDto;
import com.chugalkhorbandar.adapters.api.dto.LLMProviderInfoDto;
import com.chugalkhorbandar.adapters.api.dto.ProviderMessageDto;
import com.chugalkhorbandar.adapters.api.dto.ProviderRequestDto;
import com.chugalkhorbandar.adapters.api.dto.ProviderResponseDto;
import com.chugalkhorbandar.adapters.api.dto.ProviderTokenUsageDto;
import com.chugalkhorbandar.application.llm.LLMGenerateResult;
import com.chugalkhorbandar.application.llm.LLMProviderInfo;
import com.chugalkhorbandar.application.llm.ProviderMessage;
import com.chugalkhorbandar.application.llm.ProviderRequest;
import com.chugalkhorbandar.application.llm.ProviderResponse;
import com.chugalkhorbandar.application.llm.ProviderTokenUsage;

public final class LLMGenerateDtoMapper {

    private LLMGenerateDtoMapper() {}

    public static LLMGenerateResponseDto toDto(LLMGenerateResult result) {
        return new LLMGenerateResponseDto(
                toDto(result.providerInfo()),
                toDto(result.providerRequest()),
                toDto(result.providerResponse()));
    }

    private static LLMProviderInfoDto toDto(LLMProviderInfo info) {
        return new LLMProviderInfoDto(
                info.type().name(),
                info.name(),
                info.description(),
                info.healthy(),
                info.model());
    }

    private static ProviderRequestDto toDto(ProviderRequest request) {
        return new ProviderRequestDto(
                request.messages().stream().map(LLMGenerateDtoMapper::toDto).toList(),
                request.metadata(),
                request.temperature(),
                request.maxOutputTokens(),
                request.model());
    }

    private static ProviderMessageDto toDto(ProviderMessage message) {
        return new ProviderMessageDto(
                message.role().name(),
                message.content(),
                message.sectionType(),
                message.metadata());
    }

    private static ProviderResponseDto toDto(ProviderResponse response) {
        return new ProviderResponseDto(
                response.reply(),
                toDto(response.tokenUsage()),
                response.providerMetadata(),
                response.latencyMs(),
                response.finishReason());
    }

    private static ProviderTokenUsageDto toDto(ProviderTokenUsage usage) {
        return new ProviderTokenUsageDto(usage.promptTokens(), usage.completionTokens(), usage.totalTokens());
    }
}
