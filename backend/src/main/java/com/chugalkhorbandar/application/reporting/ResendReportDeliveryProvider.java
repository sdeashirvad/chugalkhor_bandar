package com.chugalkhorbandar.application.reporting;

import com.chugalkhorbandar.application.email.ReportEmailProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ResendReportDeliveryProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResendReportDeliveryProvider.class);

    private final ReportEmailProperties properties;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public ResendReportDeliveryProvider(ReportEmailProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newHttpClient();
    }

    public DeliveryAttemptResult deliver(ReportingEmailPayload payload, String recipient) {
        if (!properties.isConfigured()) {
            return new DeliveryAttemptResult("SKIPPED", "", 0, "Email delivery disabled or not configured");
        }
        long start = System.nanoTime();
        try {
            ResendEmailRequest request = new ResendEmailRequest(
                    payload.from(),
                    List.of(recipient),
                    payload.subject(),
                    payload.htmlBody(),
                    payload.attachments().stream().map(this::toAttachment).toList());
            String requestBody = objectMapper.writeValueAsString(request);
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.resend.com/emails"))
                    .timeout(Duration.ofSeconds(30))
                    .header("Authorization", "Bearer " + properties.getApiKey())
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            long latencyMs = (System.nanoTime() - start) / 1_000_000;
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                String messageId = extractMessageId(response.body());
                return new DeliveryAttemptResult("SENT", messageId, latencyMs, "");
            }
            LOGGER.warn("Resend delivery failed status={} recipient={}", response.statusCode(), recipient);
            return new DeliveryAttemptResult(
                    "FAILED", "", latencyMs, "Resend returned status " + response.statusCode());
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return new DeliveryAttemptResult("FAILED", "", elapsedMs(start), "Email delivery interrupted");
        } catch (IOException exception) {
            LOGGER.warn("Resend delivery failed recipient={}", recipient, exception);
            return new DeliveryAttemptResult("FAILED", "", elapsedMs(start), exception.getMessage());
        }
    }

    private ResendAttachment toAttachment(ReportingAttachment attachment) {
        String encoded = Base64.getEncoder().encodeToString(attachment.content().getBytes(StandardCharsets.UTF_8));
        return new ResendAttachment(attachment.filename(), encoded);
    }

    private String extractMessageId(String body) {
        try {
            JsonNode node = objectMapper.readTree(body);
            JsonNode id = node.get("id");
            return id == null ? "" : id.asText("");
        } catch (Exception exception) {
            return "";
        }
    }

    private static long elapsedMs(long startNano) {
        return (System.nanoTime() - startNano) / 1_000_000;
    }

    public record DeliveryAttemptResult(String status, String providerMessageId, long latencyMs, String error) {}

    record ResendEmailRequest(
            String from, List<String> to, String subject, String html, List<ResendAttachment> attachments) {}

    record ResendAttachment(String filename, String content) {}
}
