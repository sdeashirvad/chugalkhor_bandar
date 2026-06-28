package com.chugalkhorbandar.application.email;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ReportEmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReportEmailService.class);

    private final ReportEmailProperties properties;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public ReportEmailService(ReportEmailProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newHttpClient();
    }

    public EmailDeliveryResult sendDailyReport(
            String subject, String bodyHtml, String txtReport, String jsonReport, String reflection) {
        if (!properties.isConfigured()) {
            return new EmailDeliveryResult("SKIPPED", "Email delivery disabled or not configured");
        }
        try {
            String reflectionSection = reflection == null || reflection.isBlank()
                    ? ""
                    : "<p><strong>Bandar Reflection</strong></p><p>" + escapeHtml(reflection) + "</p>";
            ResendEmailRequest request = new ResendEmailRequest(
                    properties.getFrom(),
                    List.of(properties.getTo()),
                    subject,
                    bodyHtml + reflectionSection,
                    List.of(
                            attachment("jungle-daily-report.txt", txtReport),
                            attachment("jungle-daily-report.json", jsonReport)));
            String payload = objectMapper.writeValueAsString(request);
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.resend.com/emails"))
                    .timeout(Duration.ofSeconds(30))
                    .header("Authorization", "Bearer " + properties.getApiKey())
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload))
                    .build();
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return new EmailDeliveryResult("SENT", "");
            }
            LOGGER.warn("Resend email failed status={} body={}", response.statusCode(), response.body());
            return new EmailDeliveryResult("FAILED", "Resend returned status " + response.statusCode());
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return new EmailDeliveryResult("FAILED", "Email delivery interrupted");
        } catch (IOException exception) {
            LOGGER.warn("Resend email failed", exception);
            return new EmailDeliveryResult("FAILED", exception.getMessage());
        }
    }

    private static ResendAttachment attachment(String filename, String content) {
        String encoded = Base64.getEncoder().encodeToString(content.getBytes(StandardCharsets.UTF_8));
        return new ResendAttachment(filename, encoded);
    }

    private static String escapeHtml(String value) {
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    public record EmailDeliveryResult(String status, String error) {}

    record ResendEmailRequest(
            String from,
            List<String> to,
            String subject,
            String html,
            List<ResendAttachment> attachments) {}

    record ResendAttachment(String filename, String content) {}
}
