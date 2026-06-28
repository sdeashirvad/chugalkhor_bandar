package com.chugalkhorbandar.bootstrap.parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

@Component
public class FrontmatterParser {

    private final Yaml yaml = new Yaml();

    public Optional<Map<String, Object>> parseFrontmatter(Path filePath) throws IOException {
        String content = Files.readString(filePath);
        if (!content.startsWith("---")) {
            return Optional.empty();
        }

        int endIndex = content.indexOf("\n---", 3);
        if (endIndex < 0) {
            return Optional.empty();
        }

        String yamlBlock = content.substring(3, endIndex).trim();
        if (yamlBlock.isEmpty()) {
            return Optional.empty();
        }

        Object parsed = yaml.load(yamlBlock);
        if (parsed instanceof Map<?, ?> map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> frontmatter = (Map<String, Object>) map;
            return Optional.of(frontmatter);
        }
        return Optional.empty();
    }

    public boolean hasFrontmatter(Path filePath) throws IOException {
        return parseFrontmatter(filePath).isPresent();
    }
}
