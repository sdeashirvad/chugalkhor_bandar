# World Command Model

The world command model is the first true domain layer of Chugalkhor Bandar. It defines the language used to change the Jungle — independent of Spring, databases, REST APIs, LLMs, and bootstrap files.

## Philosophy

A **World Command** is an immutable intention:

> "Someone wants the world to change."

It is **not** an event (something that already happened). It is **not** a database record. It is a declarative request that may later be validated, executed, and recorded.

Every future modification path converges on this model:

| Source | Future use |
|--------|------------|
| Bootstrap | Initial world seed |
| Chat | AI-driven world changes |
| Admin | Operator corrections |
| System | Automated maintenance |
| Import | External data migration |
| Simulation | What-if scenarios |
| Migration | Schema or canon upgrades |

## Command Lifecycle

```text
Intent formed
    ↓
WorldCommand created (immutable)
    ↓
Structural validation (ids, source, timestamps)
    ↓
[Future] Business validation
    ↓
[Future] Execution against repositories
    ↓
[Future] Events emitted as side effects
```

This prompt implements only the first two stages.

## WorldCommand

Sealed interface implemented by 23 command types. Every command exposes:

| Field | Description |
|-------|-------------|
| `commandId` | Unique identifier for this command |
| `correlationId` | Groups related commands (e.g. one bootstrap batch) |
| `createdAt` | When the intention was recorded |
| `source` | `CommandSource` enum |
| `initiatedBy` | Actor that initiated the change |
| `reason` | Human-readable justification |
| `metadata` | Arbitrary key/value pairs |

### Command types

**Character:** `CreateCharacterCommand`, `UpdateCharacterCommand`, `DeleteCharacterCommand`

**Territory:** `CreateTerritoryCommand`, `TransferTerritoryCommand`, `ChangeTerritoryRulerCommand`

**Place:** `CreatePlaceCommand`, `MoveCharacterCommand`

**Story:** `CreateStoryCommand`, `LinkStoryCommand`

**Relationships:** `CreateRelationshipCommand`, `RemoveRelationshipCommand`

**Preferences:** `ChangePreferenceCommand`

**Inventory:** `CreateObjectCommand`, `TransferObjectCommand`, `ConsumeResourceCommand`

**Organizations:** `CreateOrganizationCommand`, `AssignOrganizationRoleCommand`

**Timeline:** `RecordTimelineEntryCommand`

**Prompt Profiles:** `CreatePromptProfileCommand`

**Laws:** `CreateLawCommand`

**Customs:** `CreateCustomCommand`

**Glossary:** `CreateGlossaryEntryCommand`

## Command Sources

```text
BOOTSTRAP   — Initial canon load
CHAT        — Conversational AI changes
ADMIN       — Manual operator actions
SYSTEM      — Automated processes
IMPORT      — External data ingestion
SIMULATION  — Hypothetical scenarios
MIGRATION   — Upgrade or data migration
```

## Commands Are Not Events

| World Command | Event |
|---------------|-------|
| Intention | Fact |
| May be rejected | Already happened |
| Created before execution | Emitted after execution |
| Immutable request | Immutable record of change |

Commands express *what should happen*. Events (future layer) will express *what did happen*.

## CommandMetadata

Immutable key/value container with no framework dependencies:

```java
CommandMetadata metadata = CommandMetadata.builder()
    .put("status", "ACTIVE")
    .put("version", "1.0")
    .build();
```

## WorldCommandFactory

Provides strongly typed creation methods with lightweight structural validation:

- Rejects null ids, blank strings, missing source, missing timestamps
- Does **not** validate business rules (e.g. whether a character exists)

```java
WorldCommandFactory factory = new WorldCommandFactory();
CreateCharacterCommand cmd = factory.createCharacter(
    commandId, correlationId, createdAt, CommandSource.ADMIN,
    initiatedBy, reason, metadata, characterId, title, sections);
```

## Bootstrap Mapping

`BootstrapToWorldCommandMapper` converts `BootstrapCompilation` into `List<WorldCommand>`:

```text
BootstrapCompilation
    ↓  BootstrapToWorldCommandMapper.map()
List<WorldCommand>
```

- Every bootstrap command maps to exactly one world command
- Deterministic ordering is preserved
- Source is always `BOOTSTRAP`
- Bootstrap provenance is stored in metadata (`sourceDocumentId`, `sourcePath`, `executionOrder`)

### Bootstrap type mapping

| Bootstrap Command | World Command |
|-------------------|---------------|
| CreateCanon | RecordTimelineEntry |
| CreateWorldRules | CreateLaw |
| CreatePromptProfile | CreatePromptProfile |
| CreateTerritory | CreateTerritory |
| CreatePlace | CreatePlace |
| CreateOrganization | CreateOrganization |
| CreateResource | CreateObject |
| CreateObject | CreateObject |
| CreateCharacter | CreateCharacter |
| CreateRelationship | CreateRelationship |
| CreateStory | CreateStory |
| CreateChronology | RecordTimelineEntry |
| CreateLaw | CreateLaw |
| CreateCustom | CreateCustom |
| CreateGlossaryEntry | CreateGlossaryEntry |

Canon and chronology both use `RecordTimelineEntryCommand` because the world model has no separate canon entity — foundational truth is recorded as timeline entries with full section content preserved.

## Separation from Execution

This layer:

- Does **not** execute commands
- Does **not** create runtime entities
- Does **not** persist commands
- Does **not** implement repositories or APIs

Execution belongs to a future prompt.
