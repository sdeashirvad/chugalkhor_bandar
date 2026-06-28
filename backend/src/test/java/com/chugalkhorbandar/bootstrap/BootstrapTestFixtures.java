package com.chugalkhorbandar.bootstrap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class BootstrapTestFixtures {

    private BootstrapTestFixtures() {}

    public static Path createValidBootstrap(Path root) throws IOException {
        Files.createDirectories(root.resolve("characters"));
        Files.createDirectories(root.resolve("stories"));
        Files.createDirectories(root.resolve("prompts"));
        Files.createDirectories(root.resolve("chronology"));

        writeManifest(root);
        writeMarkdown(
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
        writeMarkdown(
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
        writeMarkdown(
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
        writeMarkdown(
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
        writeMarkdown(
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

        return root;
    }

    public static void writeManifest(Path root) throws IOException {
        Files.writeString(
                root.resolve("manifest.yaml"),
                """
                worldId: test_world
                worldName: Test World
                bootstrapVersion: "1.0"
                schemaVersion: "1"
                createdBy: test
                createdAt: "2026-01-01"
                language: en
                """);
    }

    public static void writeMarkdown(Path path, String content) throws IOException {
        Files.createDirectories(path.getParent());
        Files.writeString(path, content);
    }
}
