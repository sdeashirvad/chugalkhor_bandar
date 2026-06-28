package com.chugalkhorbandar.bootstrap.typed;

import com.chugalkhorbandar.bootstrap.BootstrapTestFixtures;
import com.chugalkhorbandar.bootstrap.BootstrapDocumentLoader;
import com.chugalkhorbandar.bootstrap.document.model.BootstrapDocument;
import com.chugalkhorbandar.bootstrap.document.BootstrapDocumentReader;
import com.chugalkhorbandar.bootstrap.document.BootstrapDocumentRepository;
import com.chugalkhorbandar.bootstrap.scanner.BootstrapScanner;
import com.chugalkhorbandar.bootstrap.parser.FrontmatterParser;
import com.chugalkhorbandar.bootstrap.parser.ManifestParser;
import java.io.IOException;
import java.nio.file.Path;

public final class TypedBootstrapTestFixtures {

    private TypedBootstrapTestFixtures() {}

    public static void enrichForTypedLoading(Path root) throws IOException {
        BootstrapTestFixtures.writeMarkdown(
                root.resolve("characters/hero.md"),
                """
                ---
                id: character_hero
                name: Hero
                version: 1.0
                status: ACTIVE
                ---

                # Hero

                ## Summary
                A brave hero.
                """);
        BootstrapTestFixtures.writeMarkdown(
                root.resolve("stories/origin.md"),
                """
                ---
                id: story_origin
                title: Origin Story
                version: 1.0
                status: ACTIVE
                ---

                # Origin

                ## Summary
                The origin tale.
                """);
        BootstrapTestFixtures.writeMarkdown(
                root.resolve("prompts/guide.md"),
                """
                ---
                id: prompt_guide
                title: Guide
                version: 1.0
                status: ACTIVE
                ---

                # Guide

                # Identity
                Guide identity text.
                """);
        BootstrapTestFixtures.writeMarkdown(
                root.resolve("chronology/timeline.md"),
                """
                ---
                id: world_timeline
                title: Timeline
                version: 1.0
                status: ACTIVE
                ---

                # Timeline

                ## Ancient Era
                Early events.
                """);
        BootstrapTestFixtures.writeMarkdown(
                root.resolve("canon.md"),
                """
                ---
                id: canon
                title: Canon
                version: 1.0
                status: ACTIVE
                ---

                # Canon

                ## The World
                World facts.
                """);
        BootstrapTestFixtures.writeMarkdown(
                root.resolve("places.md"),
                """
                ---
                id: places
                title: Places
                version: 1.0
                status: ACTIVE
                ---

                # Places

                # Home Jungle
                ## Description
                Capital jungle.
                """);
    }

    public static BootstrapDocumentRepository loadRepository(Path root) throws IOException {
        BootstrapTestFixtures.createValidBootstrap(root);
        var scanner = new BootstrapScanner(new ManifestParser(), new FrontmatterParser());
        var world = scanner.scan(root);
        var repository = new BootstrapDocumentRepository();
        new BootstrapDocumentLoader().loadIntoRepository(world, repository);
        return repository;
    }

    public static BootstrapDocument loadDocument(Path root, String relativePath) throws IOException {
        var reader = new BootstrapDocumentReader();
        return reader.read(root, root.resolve(relativePath));
    }
}
