# Bootstrap Document Engine

The generic markdown document engine parses bootstrap canon files into immutable `BootstrapDocument` instances. It has no knowledge of characters, stories, places, or persistence — only markdown structure.

## Parsing Pipeline

```
Path (markdown file)
    │
    ├─► FrontmatterParser  ──► DocumentMetadata
    │
    └─► MarkdownBodyParser ──► DocumentBody
              │
              ▼
        BootstrapDocument
```

`BootstrapDocumentReader` combines frontmatter and body parsing. `DocumentTypeResolver` infers type from folder or filename — no business semantics.

## Supported Markdown

Every validated file is expected to follow this structure:

```markdown
---
id: example_id
title: Example Title
version: 1.0
status: ACTIVE
---

# Document Heading

## Section One

Section content.

## Section Two

More content.
```

### Frontmatter

Parsed into `DocumentMetadata` (unchanged from validation framework). Character files may use `name` instead of `title`.

### Body rules

| Element | Syntax | Behaviour |
|---------|--------|-----------|
| H1 heading | `# Title` | First `#` line becomes the document heading |
| H2 section | `## Section` | Starts a new ordered section |
| Legacy section | `# Section` after H1 | Treated as a section boundary (backward compatible) |
| Other lines | plain text | Appended to the current section content |

The parser:

- Strips YAML frontmatter before parsing the body
- Normalizes line endings to `\n`
- Preserves section order (0-based `order` field)
- Does **not** interpret section content

## Models

### BootstrapDocument

| Field | Description |
|-------|-------------|
| `metadata` | `DocumentMetadata` from frontmatter |
| `heading` | First H1 title |
| `sections` | Ordered list of `DocumentSection` |
| `originalMarkdown` | Full normalized file content |
| `sourcePath` | Absolute path to source file |
| `documentType` | Inferred from folder/filename |

### DocumentSection

| Field | Description |
|-------|-------------|
| `title` | Section heading text |
| `content` | Body text (trimmed trailing whitespace) |
| `order` | Zero-based position in document |

### DocumentBody

Immutable container for `heading` and `sections`.

## Section Lookup API

All lookups are **case-insensitive**.

```java
Optional<DocumentSection> findSection(String title);
boolean hasSection(String title);
List<DocumentSection> getSections();
String getContent(String title);           // null if not found
Optional<String> getContentOptional(String title);
```

## Ordering Guarantees

- Sections appear in the order they occur in the source file
- `order` is assigned sequentially starting at 0
- Re-parsing the same file always produces the same section order

## Document Repository

`BootstrapDocumentRepository` is an in-memory store populated after validation succeeds.

```java
Optional<BootstrapDocument> findById(String id);
List<BootstrapDocument> findByType(DocumentType type);
List<BootstrapDocument> findAll();
```

`DocumentType` is inferred from path:

| Location | Type |
|----------|------|
| `characters/` | `CHARACTER` |
| `stories/` | `STORY` |
| `prompts/` | `PROMPT` |
| `chronology/` | `CHRONOLOGY` |
| `places.md` | `PLACES` |
| `resources.md` | `RESOURCES` |
| `objects.md` | `OBJECTS` |
| … | (see `DocumentType` enum) |

## Body Validation

Extended validation rules (in addition to metadata rules):

| Rule | Severity | Description |
|------|----------|-------------|
| Missing H1 | ERROR | Document body must contain at least one `#` heading |
| Duplicate section | WARNING | Duplicate section titles (case-insensitive) |

Warnings do not block startup. Errors block startup.

## Startup Loading

After validation passes, `BootstrapDocumentLoadingRunner` parses every validated markdown file and logs:

```text
Bootstrap Documents Loaded

Characters .......... 13
Stories ............. 3
Places .............. 1
Resources ........... 1
Objects ............. 1
Total Documents ..... XX
```

## Out of Scope

This engine does **not**:

- Parse character personalities or story plots
- Validate required sections per document type
- Seed databases or create domain objects
- Build world graphs or events

Typed readers in future prompts will consume `BootstrapDocument` from the repository.
