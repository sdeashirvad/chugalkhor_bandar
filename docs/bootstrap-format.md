# Bootstrap Format

This document defines the contract for Chugalkhor Bandar bootstrap canon files. Anyone adding or editing bootstrap content must follow these rules so the validation framework can guarantee internal consistency before data is ever loaded into a database.

## Directory Structure

```text
bootstrap/

manifest.yaml

README.md                 # documentation only (not validated as canon)

canon.md
world-rules.md
glossary.md
territories.md
places.md
relationships.md
organizations.md
laws.md
customs.md
resources.md
objects.md

chronology/
    *.md

stories/
    *.md

characters/
    *.md

prompts/
    *.md
```

The scanner discovers markdown files in `characters/`, `stories/`, `prompts/`, `chronology/`, and known reference documents at the bootstrap root.

## Manifest

Every bootstrap world must include `manifest.yaml` at the root.

```yaml
worldId: chugalkhor_bandar
worldName: Chugalkhor Bandar
bootstrapVersion: "1.0"
schemaVersion: "1"
createdBy: bootstrap-architecture
createdAt: "2026-06-27"
language: en
```

### Required fields

| Field | Description |
|-------|-------------|
| `worldId` | Stable identifier for the world |
| `worldName` | Human-readable world name |
| `bootstrapVersion` | Version of this bootstrap package |
| `schemaVersion` | Validation schema version |
| `createdBy` | Author or system that created the bootstrap |
| `createdAt` | Creation date (ISO-8601 date string) |
| `language` | Primary language code (e.g. `en`) |

If the manifest is missing or incomplete, application startup fails.

## Frontmatter

Every validated markdown file must begin with YAML frontmatter:

```markdown
---
id: character_example
title: Example Character
version: 1.0
status: ACTIVE
---

# Heading

Body content...
```

### Required metadata fields

| Field | Description |
|-------|-------------|
| `id` | Globally unique document identifier |
| `title` | Display title (`name` is accepted for character files) |
| `version` | Document version |
| `status` | Lifecycle status (see below) |
| `filePath` | Assigned automatically by the scanner |

The markdown body is not parsed during validation yet.

### Allowed status values

| Status | Meaning |
|--------|---------|
| `ACTIVE` | Canonical, in-use content |
| `DRAFT` | Work in progress |
| `DEPRECATED` | Superseded but retained |
| `ARCHIVED` | Retained for history only |

Any other status value is rejected.

## ID Conventions

IDs must be unique across the entire bootstrap tree.

Recommended prefixes:

| Category | Prefix | Example |
|----------|--------|---------|
| Character | `character_` | `character_bandar` |
| Story | `story_` | `story_founding_hippu_dynasty` |
| Prompt | `prompt_` | `prompt_bandar_personality` |
| Chronology | any unique id | `world_timeline` |
| Reference | descriptive | `canon`, `world-rules` |

## Validation Rules

At startup the application:

1. Verifies the bootstrap directory exists.
2. Scans all bootstrap files.
3. Parses manifest and frontmatter metadata.
4. Runs validation rules.
5. Produces an immutable validation report.
6. Fails startup if any error is found.

### Generic rules

| Rule | Description |
|------|-------------|
| Manifest | `manifest.yaml` must exist with all required fields |
| Missing frontmatter | Validated markdown files must have YAML frontmatter |
| Required fields | `id`, `title`, `version`, and `status` must be present |
| Empty ID | `id` must not be blank |
| Invalid status | Status must be one of the allowed values |
| Duplicate ID | No two files may share the same `id` |
| Duplicate filename | No two validated files in the same directory may share the same filename |
| Missing H1 | Document body must contain an H1 (`#`) heading |
| Duplicate section | Duplicate H2 (or legacy section) headings produce a warning |

### Category rules

| Category | Additional rules |
|----------|------------------|
| Characters | `id` must start with `character_`; requires `title` or `name` |
| Stories | `id` must start with `story_` |
| Prompts | `id` must start with `prompt_` |
| Chronology | Requires standard metadata fields |

## Validation Report

On success, startup logs:

```text
Loading Bootstrap...

Manifest ............ OK
Characters .......... 13
Stories ............. 3
Timeline ............ 1

Validation ......... PASSED
```

On failure, startup stops with a summary report listing errors and affected file paths.

## What Is Not Validated Yet

The following are intentionally out of scope for this milestone:

- Markdown body parsing
- Cross-reference validation between documents
- Database seeding
- REST APIs
- World graph construction

Those belong to later implementation prompts.
