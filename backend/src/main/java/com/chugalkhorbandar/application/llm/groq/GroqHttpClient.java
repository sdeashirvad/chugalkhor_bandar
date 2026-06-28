package com.chugalkhorbandar.application.llm.groq;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

public class GroqHttpClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(GroqHttpClient.class);

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String baseUrl;
    private final int timeoutSeconds;
    private final boolean logRequests;

    public GroqHttpClient(ObjectMapper objectMapper, String baseUrl, int timeoutSeconds, Environment environment) {
        this(objectMapper, baseUrl, timeoutSeconds, environment, HttpClient.newHttpClient());
    }

    GroqHttpClient(
            ObjectMapper objectMapper,
            String baseUrl,
            int timeoutSeconds,
            Environment environment,
            HttpClient httpClient) {
        this.objectMapper = objectMapper;
        this.baseUrl = normalizeBaseUrl(baseUrl);
        this.timeoutSeconds = timeoutSeconds;
        this.logRequests = isDeveloperMode(environment);
        this.httpClient = httpClient;
    }

    public GroqChatCompletionResult chatCompletion(String apiKey, GroqChatCompletionRequest request)
            throws GroqHttpException {
        String requestBody;
        try {
            requestBody = objectMapper.writeValueAsString(request);
        } catch (IOException exception) {
            throw new GroqHttpException("Failed to serialize Groq request", 0, false, exception);
        }

        if (logRequests) {
            LOGGER.info(
                    "Groq chat completion request model={} messageCount={}",
                    request.model(),
                    request.messages().size());
        }

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/chat/completions"))
                .timeout(Duration.ofSeconds(timeoutSeconds))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response;
        try {
            response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new GroqHttpException("Groq request interrupted", 0, true, exception);
        } catch (IOException exception) {
            throw new GroqHttpException("Groq request failed", 0, true, exception);
        }

        if (logRequests) {
            LOGGER.info("Groq chat completion response status={}", response.statusCode());
        }

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return parseSuccessResponse(response.body());
        }

        throw new GroqHttpException(
                mapErrorMessage(response.statusCode(), response.body()),
                response.statusCode(),
                isRetryableStatus(response.statusCode()));
    }

    private GroqChatCompletionResult parseSuccessResponse(String body) throws GroqHttpException {
        try {
            GroqChatCompletionResponse parsed = objectMapper.readValue(body, GroqChatCompletionResponse.class);
            String reply = parsed.choices().stream()
                    .findFirst()
                    .map(choice -> choice.message().content())
                    .orElse("");
            String finishReason = parsed.choices().stream()
                    .findFirst()
                    .map(GroqChatCompletionResponse.Choice::finishReason)
                    .orElse("stop");
            GroqChatCompletionResponse.Usage usage = parsed.usage() == null
                    ? new GroqChatCompletionResponse.Usage(0, 0, 0)
                    : parsed.usage();
            return new GroqChatCompletionResult(
                    reply,
                    finishReason,
                    usage.promptTokens(),
                    usage.completionTokens(),
                    usage.totalTokens());
        } catch (IOException exception) {
            throw new GroqHttpException("Failed to parse Groq response", 0, false, exception);
        }
    }

    static boolean isRetryableStatus(int statusCode) {
        return statusCode == 429 || statusCode >= 500;
    }

    private static String mapErrorMessage(int statusCode, String body) {
        return switch (statusCode) {
            case 429 -> "Groq rate limit or quota exceeded";
            case 401, 403 -> "Groq authentication failed";
            default -> statusCode >= 500 ? "Groq temporary provider error" : "Groq request rejected";
        };
    }

    private static String normalizeBaseUrl(String baseUrl) {
        if (baseUrl == null || baseUrl.isBlank()) {
            return "https://api.groq.com/openai/v1";
        }
        String trimmed = baseUrl.trim();
        return trimmed.endsWith("/") ? trimmed.substring(0, trimmed.length() - 1) : trimmed;
    }

    private static boolean isDeveloperMode(Environment environment) {
        if (environment == null) {
            return false;
        }
        return environment.acceptsProfiles(org.springframework.core.env.Profiles.of("dev"));
    }

    public record GroqChatCompletionRequest(
            String model,
            List<GroqMessage> messages,
            double temperature,
            @JsonProperty("max_tokens") int maxTokens) {}

    public record GroqMessage(String role, String content) {}

    public record GroqChatCompletionResult(
            String reply,
            String finishReason,
            int promptTokens,
            int completionTokens,
            int totalTokens) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    record GroqChatCompletionResponse(List<Choice> choices, Usage usage) {

        @JsonIgnoreProperties(ignoreUnknown = true)
        record Choice(GroqMessage message, @JsonProperty("finish_reason") String finishReason) {}

        @JsonIgnoreProperties(ignoreUnknown = true)
        record Usage(
                @JsonProperty("prompt_tokens") int promptTokens,
                @JsonProperty("completion_tokens") int completionTokens,
                @JsonProperty("total_tokens") int totalTokens) {}
    }
}
