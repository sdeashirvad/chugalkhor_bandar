package com.chugalkhorbandar.bootstrap.parser;

import com.chugalkhorbandar.bootstrap.model.Manifest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

@Component
public class ManifestParser {

    private final Yaml yaml = new Yaml();

    public Manifest parse(Path manifestPath) throws IOException {
        String content = Files.readString(manifestPath);
        Object parsed = yaml.load(content);
        if (!(parsed instanceof Map<?, ?> map)) {
            throw new IllegalArgumentException("Manifest must be a YAML mapping: " + manifestPath);
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> fields = (Map<String, Object>) map;

        return new Manifest(
                stringValue(fields.get("worldId")),
                stringValue(fields.get("worldName")),
                stringValue(fields.get("bootstrapVersion")),
                stringValue(fields.get("schemaVersion")),
                stringValue(fields.get("createdBy")),
                stringValue(fields.get("createdAt")),
                stringValue(fields.get("language")),
                manifestPath);
    }

    private static String stringValue(Object value) {
        if (value == null) {
            return null;
        }
        String text = value.toString().trim();
        return text.isEmpty() ? null : text;
    }
}
