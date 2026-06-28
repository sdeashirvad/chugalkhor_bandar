package com.chugalkhorbandar.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class DotEnvLoader {

    private DotEnvLoader() {}

    public static Map<String, String> load() {
        Path envFile = resolveEnvFile();
        if (!Files.isRegularFile(envFile)) {
            return Map.of();
        }

        Map<String, String> properties = new LinkedHashMap<>();
        try {
            List<String> lines = Files.readAllLines(envFile);
            for (String line : lines) {
                parseLine(line).ifPresent(entry -> properties.put(entry.key(), entry.value()));
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to read .env file at " + envFile, exception);
        }
        return Map.copyOf(properties);
    }

    /** Applies .env entries before Spring starts so ${POSTGRES_*} placeholders resolve reliably. */
    public static void applyToSystemProperties() {
        Map<String, String> loaded = load();
        if (loaded.isEmpty()) {
            return;
        }

        Map<String, Object> properties = new LinkedHashMap<>(loaded);
        mapEnvToSpringProperties(properties);

        loaded.forEach(DotEnvLoader::setSystemPropertyIfAbsent);
        properties.forEach((key, value) -> setSystemPropertyIfAbsent(key, String.valueOf(value)));
    }

    private static void setSystemPropertyIfAbsent(String key, String value) {
        if (value == null || value.isBlank()) {
            return;
        }
        if (System.getProperty(key) == null && System.getenv(key) == null) {
            System.setProperty(key, value);
        }
    }

    public static Path resolveEnvFile() {
        Path cwd = Path.of("").toAbsolutePath().normalize();
        Path backendEnv = cwd.resolve(".env");
        if (Files.isRegularFile(backendEnv)) {
            return backendEnv;
        }
        Path nestedEnv = cwd.resolve("backend").resolve(".env");
        if (Files.isRegularFile(nestedEnv)) {
            return nestedEnv;
        }
        return backendEnv;
    }

    static void mapEnvToSpringProperties(Map<String, Object> properties) {
        mapPostgresProperties(properties);
        copyEnv(properties, "LLM_PROVIDER", "llm.provider");
        copyEnv(properties, "GROQ_MODEL", "llm.model");
        copyEnv(properties, "LLM_TEMPERATURE", "llm.temperature");
        copyEnv(properties, "LLM_MAX_OUTPUT_TOKENS", "llm.max-output-tokens");
        copyEnv(properties, "LLM_TIMEOUT_SECONDS", "llm.timeout-seconds");
        copyEnv(properties, "GROQ_BASE_URL", "groq.base-url");
        copyEnv(properties, "GROQ_API_KEY_1", "groq.api-key-1");
        copyEnv(properties, "GROQ_API_KEY_2", "groq.api-key-2");
    }

    private static void mapPostgresProperties(Map<String, Object> properties) {
        copyEnv(properties, "POSTGRES_URL", "spring.datasource.url");
        if (!properties.containsKey("spring.datasource.url")) {
            buildPostgresJdbcUrl(properties).ifPresent(url -> properties.put("spring.datasource.url", url));
        }
        copyEnv(properties, "POSTGRES_USER", "spring.datasource.username");
        copyEnv(properties, "POSTGRES_PASSWORD", "spring.datasource.password");
    }

    private static Optional<String> buildPostgresJdbcUrl(Map<String, Object> properties) {
        String host = textValue(properties.get("POSTGRES_HOST"));
        String database = textValue(properties.get("POSTGRES_DB"));
        if (host == null || database == null) {
            return Optional.empty();
        }
        String port = textValue(properties.get("POSTGRES_PORT"));
        if (port == null) {
            port = "5432";
        }
        String sslMode = textValue(properties.get("POSTGRES_SSLMODE"));
        if (sslMode == null) {
            sslMode = "require";
        }
        return Optional.of(
                "jdbc:postgresql://" + host + ":" + port + "/" + database + "?sslmode=" + sslMode);
    }

    private static String textValue(Object value) {
        if (value == null) {
            return null;
        }
        String text = value.toString().trim();
        return text.isEmpty() ? null : text;
    }

    private static void copyEnv(Map<String, Object> properties, String envKey, String springKey) {
        Object value = properties.get(envKey);
        if (value != null && !value.toString().isBlank()) {
            properties.put(springKey, value);
        }
    }

    private static Optional<EnvEntry> parseLine(String line) {
        String trimmed = line.trim();
        if (trimmed.isEmpty() || trimmed.startsWith("#")) {
            return Optional.empty();
        }
        int separator = trimmed.indexOf('=');
        if (separator <= 0) {
            return Optional.empty();
        }
        String key = trimmed.substring(0, separator).trim();
        if (key.startsWith("\uFEFF")) {
            key = key.substring(1);
        }
        String value = trimmed.substring(separator + 1).trim();
        if ((value.startsWith("\"") && value.endsWith("\"")) || (value.startsWith("'") && value.endsWith("'"))) {
            value = value.substring(1, value.length() - 1);
        }
        return Optional.of(new EnvEntry(key, value));
    }

    private record EnvEntry(String key, String value) {}
}
