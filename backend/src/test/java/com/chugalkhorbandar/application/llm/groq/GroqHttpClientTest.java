package com.chugalkhorbandar.application.llm.groq;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

class GroqHttpClientTest {

    private com.sun.net.httpserver.HttpServer server;
    private String baseUrl;
    private final AtomicInteger requestCount = new AtomicInteger();

    @BeforeEach
    void setUp() throws IOException {
        server = com.sun.net.httpserver.HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/openai/v1/chat/completions", exchange -> {
            requestCount.incrementAndGet();
            byte[] body = exchange.getRequestBody().readAllBytes();
            assertThat(new String(body, StandardCharsets.UTF_8)).contains("\"model\":\"test-model\"");
            String response =
                    """
                    {
                      "choices": [{
                        "message": {"role": "assistant", "content": "Hello from Groq"},
                        "finish_reason": "stop"
                      }],
                      "usage": {"prompt_tokens": 12, "completion_tokens": 5, "total_tokens": 17}
                    }
                    """;
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.length());
            try (OutputStream outputStream = exchange.getResponseBody()) {
                outputStream.write(response.getBytes(StandardCharsets.UTF_8));
            }
        });
        server.start();
        baseUrl = "http://localhost:" + server.getAddress().getPort() + "/openai/v1";
    }

    @AfterEach
    void tearDown() {
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    void parsesSuccessfulChatCompletionResponse() throws Exception {
        GroqHttpClient client = new GroqHttpClient(
                new ObjectMapper(), baseUrl, 5, new MockEnvironment(), java.net.http.HttpClient.newHttpClient());

        GroqHttpClient.GroqChatCompletionResult result = client.chatCompletion(
                "test-key",
                new GroqHttpClient.GroqChatCompletionRequest(
                        "test-model",
                        List.of(new GroqHttpClient.GroqMessage("user", "Hi")),
                        0.7,
                        128));

        assertThat(requestCount.get()).isEqualTo(1);
        assertThat(result.reply()).isEqualTo("Hello from Groq");
        assertThat(result.finishReason()).isEqualTo("stop");
        assertThat(result.promptTokens()).isEqualTo(12);
        assertThat(result.completionTokens()).isEqualTo(5);
        assertThat(result.totalTokens()).isEqualTo(17);
    }

    @Test
    void marksRateLimitAsRetryable() {
        assertThat(GroqHttpClient.isRetryableStatus(429)).isTrue();
        assertThat(GroqHttpClient.isRetryableStatus(503)).isTrue();
        assertThat(GroqHttpClient.isRetryableStatus(400)).isFalse();
    }
}
