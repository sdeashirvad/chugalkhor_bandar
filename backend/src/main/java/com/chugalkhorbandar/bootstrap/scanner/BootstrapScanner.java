package com.chugalkhorbandar.bootstrap.scanner;

import com.chugalkhorbandar.bootstrap.model.BootstrapFile;
import com.chugalkhorbandar.bootstrap.model.BootstrapFileCategory;
import com.chugalkhorbandar.bootstrap.model.BootstrapWorld;
import com.chugalkhorbandar.bootstrap.model.DocumentMetadata;
import com.chugalkhorbandar.bootstrap.model.Manifest;
import com.chugalkhorbandar.bootstrap.parser.FrontmatterParser;
import com.chugalkhorbandar.bootstrap.parser.ManifestParser;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import org.springframework.stereotype.Component;

@Component
public class BootstrapScanner {

    private static final Set<String> EXCLUDED_FILES = Set.of("README.md");

    private static final Set<String> REFERENCE_FILES = Set.of(
            "canon.md",
            "world-rules.md",
            "glossary.md",
            "territories.md",
            "places.md",
            "relationships.md",
            "organizations.md",
            "laws.md",
            "customs.md",
            "resources.md",
            "objects.md",
            "family-tree.md",
            "narrative-rules.md");

    private final ManifestParser manifestParser;
    private final FrontmatterParser frontmatterParser;

    public BootstrapScanner(ManifestParser manifestParser, FrontmatterParser frontmatterParser) {
        this.manifestParser = manifestParser;
        this.frontmatterParser = frontmatterParser;
    }

    public BootstrapWorld scan(Path rootPath) {
        Path normalizedRoot = rootPath.toAbsolutePath().normalize();

        Optional<Manifest> manifest = loadManifest(normalizedRoot);
        List<BootstrapFile> characters = scanDirectory(normalizedRoot.resolve("characters"), BootstrapFileCategory.CHARACTER);
        List<BootstrapFile> stories = scanDirectory(normalizedRoot.resolve("stories"), BootstrapFileCategory.STORY);
        List<BootstrapFile> prompts = scanDirectory(normalizedRoot.resolve("prompts"), BootstrapFileCategory.PROMPT);
        List<BootstrapFile> chronology = scanDirectory(normalizedRoot.resolve("chronology"), BootstrapFileCategory.CHRONOLOGY);
        List<BootstrapFile> references = scanReferenceFiles(normalizedRoot);

        List<BootstrapFile> allValidatedFiles = new ArrayList<>();
        allValidatedFiles.addAll(characters);
        allValidatedFiles.addAll(stories);
        allValidatedFiles.addAll(prompts);
        allValidatedFiles.addAll(chronology);
        allValidatedFiles.addAll(references);

        return new BootstrapWorld(
                normalizedRoot,
                manifest,
                characters,
                stories,
                prompts,
                chronology,
                references,
                allValidatedFiles);
    }

    private Optional<Manifest> loadManifest(Path rootPath) {
        Path manifestPath = rootPath.resolve("manifest.yaml");
        if (!Files.isRegularFile(manifestPath)) {
            return Optional.empty();
        }
        try {
            return Optional.of(manifestParser.parse(manifestPath));
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read manifest: " + manifestPath, e);
        }
    }

    private List<BootstrapFile> scanReferenceFiles(Path rootPath) {
        List<BootstrapFile> files = new ArrayList<>();
        try (Stream<Path> paths = Files.list(rootPath)) {
            paths.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".md"))
                    .filter(path -> REFERENCE_FILES.contains(path.getFileName().toString()))
                    .map(path -> toBootstrapFile(path, BootstrapFileCategory.REFERENCE))
                    .forEach(files::add);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to scan reference files in: " + rootPath, e);
        }
        return files;
    }

    private List<BootstrapFile> scanDirectory(Path directory, BootstrapFileCategory category) {
        if (!Files.isDirectory(directory)) {
            return List.of();
        }
        try (Stream<Path> paths = Files.walk(directory)) {
            return paths.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".md"))
                    .map(path -> toBootstrapFile(path, category))
                    .toList();
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to scan directory: " + directory, e);
        }
    }

    private BootstrapFile toBootstrapFile(Path filePath, BootstrapFileCategory category) {
        if (EXCLUDED_FILES.contains(filePath.getFileName().toString())) {
            return new BootstrapFile(filePath, BootstrapFileCategory.EXCLUDED, Optional.empty());
        }

        try {
            Optional<Map<String, Object>> frontmatter = frontmatterParser.parseFrontmatter(filePath);
            Optional<DocumentMetadata> metadata =
                    frontmatter.map(map -> DocumentMetadata.fromFrontmatter(map, filePath));
            return new BootstrapFile(filePath, category, metadata);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read bootstrap file: " + filePath, e);
        }
    }
}
