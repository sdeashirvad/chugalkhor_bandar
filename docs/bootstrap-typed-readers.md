# Bootstrap Typed Readers

The typed reader layer converts generic `BootstrapDocument` instances into strongly typed bootstrap specs (DTOs). It consumes only `BootstrapDocumentRepository` — it never reads the filesystem or parses markdown directly.

## Typed Reader Contract

```java
public interface BootstrapTypedReader<T> {
    boolean supports(DocumentType type);
    T read(BootstrapDocument document);
}
```

Each reader:

1. Accepts a `BootstrapDocument` from the repository
2. Maps known section titles to typed fields
3. Preserves unknown sections in `unmappedSections`
4. Validates required sections for its contract
5. Returns an immutable spec
6. Fails fast with `TypedReaderException` when required content is missing

## Common Spec Fields

Every typed spec exposes:

| Field | Source |
|-------|--------|
| `id` | Frontmatter `id` |
| `title` | Frontmatter `title` (or `name` for characters) |
| `sourcePath` | Document file path |
| `status` | Frontmatter `status` |
| `version` | Frontmatter `version` |
| `documentType` | Inferred document type |

## Supported Document Types

| DocumentType | Reader | Spec |
|--------------|--------|------|
| `CHARACTER` | `CharacterBootstrapReader` | `CharacterBootstrapSpec` |
| `STORY` | `StoryBootstrapReader` | `StoryBootstrapSpec` |
| `PLACES` | `PlaceBootstrapReader` | `PlaceBootstrapSpec` |
| `TERRITORIES` | `TerritoryBootstrapReader` | `TerritoryBootstrapSpec` |
| `ORGANIZATIONS` | `OrganizationBootstrapReader` | `OrganizationBootstrapSpec` |
| `RESOURCES` | `ResourceBootstrapReader` | `ResourceBootstrapSpec` |
| `OBJECTS` | `ObjectBootstrapReader` | `ObjectBootstrapSpec` |
| `RELATIONSHIPS` | `RelationshipBootstrapReader` | `RelationshipBootstrapSpec` |
| `LAWS` | `LawBootstrapReader` | `LawBootstrapSpec` |
| `CUSTOMS` | `CustomBootstrapReader` | `CustomBootstrapSpec` |
| `GLOSSARY` | `GlossaryBootstrapReader` | `GlossaryEntryBootstrapSpec` |
| `PROMPT` | `PromptProfileBootstrapReader` | `PromptProfileBootstrapSpec` |
| `CANON` | `CanonBootstrapReader` | `CanonBootstrapSpec` |
| `WORLD_RULES` | `WorldRulesBootstrapReader` | `WorldRulesBootstrapSpec` |
| `CHRONOLOGY` | `ChronologyBootstrapReader` | `ChronologyBootstrapSpec` |

Ignored types: `REFERENCE`, `FAMILY_TREE`, `NARRATIVE_RULES`.

## Required Sections

| Type | Required sections |
|------|-------------------|
| Character | `Summary` |
| Story | `Summary` |
| Prompt profile | `Identity` |
| Place, Territory, Organization, Resource, Object, Relationship, Law, Custom, Glossary, Canon, World Rules, Chronology | At least one section |

Empty required sections cause `TypedReaderException`.

## Registry Behaviour

`BootstrapTypedReaderRegistry` maps each `DocumentType` to exactly one reader. Spring injects all `BootstrapTypedReader` beans at startup.

```java
registry.isSupported(DocumentType.CHARACTER);  // true
registry.read(document);                         // CharacterBootstrapSpec
```

## Aggregation Model

`BootstrapTypedWorld` holds lists of typed specs:

```text
BootstrapTypedWorld
├── characters
├── stories
├── places
├── territories
├── organizations
├── resources
├── objects
├── relationships
├── laws
├── customs
├── glossaryEntries
├── promptProfiles
├── canon
├── worldRules
└── chronologyEntries
```

`BootstrapTypedLoadingService` reads all documents from the repository, routes each through the registry, and builds the world.

After loading, `BootstrapTypedWorldHolder` exposes the aggregated world.

## Unknown Sections

Sections not mapped to known fields are stored in `unmappedSections` (title → content). Unknown sections do not fail the reader.

## Startup Log

```text
Typed Bootstrap Loaded

Characters .......... 13
Stories ............. 3
Places .............. 1
Territories ......... 1
Organizations ....... 1
Resources ........... 1
Objects ............. 1
Relationships ....... 1
Laws ................ 1
Customs ............. 1
Glossary ............ 1
Prompt Profiles ..... 1
Chronology .......... 1
```

## Out of Scope

This layer does **not**:

- Create runtime world entities
- Seed databases
- Parse markdown files directly
- Build world graphs or events

Typed specs are pure bootstrap DTOs for downstream engines.
