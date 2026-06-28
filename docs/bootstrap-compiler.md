# Bootstrap Compiler

The bootstrap compiler is a pure transformation layer. It converts `BootstrapTypedWorld` (the AST) into `BootstrapCompilation` (bytecode) — an ordered sequence of immutable commands ready for execution by repositories in a later phase.

## Pipeline

```text
Markdown files
    ↓  (validation + document engine + typed readers)
BootstrapTypedWorld
    ↓  BootstrapCompiler.compile()
BootstrapCompilation
    ↓  (future: command executor)
Repositories / database
```

| Stage | Analogy | Mutable? |
|-------|---------|----------|
| Markdown | Source code | Yes (authoring) |
| BootstrapTypedWorld | AST | Immutable specs |
| BootstrapCompilation | Bytecode | Immutable commands |
| Repository execution | Runtime | Future prompt |

## Why Commands Exist

Commands decouple **compilation** from **execution**:

- The compiler has no knowledge of PostgreSQL, H2, or Spring Boot.
- Commands capture *what* to create, not *how* to persist it.
- The same compilation output can be inspected, logged, replayed, or executed later.
- Ordering is explicit via `executionOrder`.

## BootstrapCommand

Sealed interface implemented by 15 command types:

- `CreateCanonCommand`
- `CreateWorldRulesCommand`
- `CreatePromptProfileCommand`
- `CreateTerritoryCommand`
- `CreatePlaceCommand`
- `CreateOrganizationCommand`
- `CreateResourceCommand`
- `CreateObjectCommand`
- `CreateCharacterCommand`
- `CreateRelationshipCommand`
- `CreateStoryCommand`
- `CreateChronologyCommand`
- `CreateLawCommand`
- `CreateCustomCommand`
- `CreateGlossaryEntryCommand`

Every command exposes:

| Field | Description |
|-------|-------------|
| `commandId` | Unique identifier (document id) |
| `executionOrder` | Zero-based position in compilation |
| `sourceDocumentId` | Originating bootstrap document id |
| `sourcePath` | Originating file path |
| `commandType()` | Command type name |

Type-specific fields include entity id, title, `sections` map, and `metadata` map.

## Deterministic Compilation

Commands are always emitted in this category order:

1. Canon
2. World Rules
3. Prompt Profiles
4. Territories
5. Places
6. Organizations
7. Resources
8. Objects
9. Characters
10. Relationships
11. Stories
12. Chronology
13. Laws
14. Customs
15. Glossary

Within each category, specs are sorted by `id`. The compiler never depends on filesystem ordering.

Identical `BootstrapTypedWorld` input always produces identical command sequences.

## Compiler Validation

The compiler rejects:

| Condition | Result |
|-----------|--------|
| Null `BootstrapTypedWorld` | `BootstrapCompilationException` |
| Null spec | `BootstrapCompilationException` |
| Missing source document id | `BootstrapCompilationException` |
| Duplicate command ids | `BootstrapCompilationException` |

Document-level validation is assumed to have already succeeded in earlier layers.

## BootstrapCompilation

Immutable record containing:

- `commands` — ordered, unmodifiable command list
- `warnings` — compilation warnings (empty for now)
- `report` — `BootstrapCompilationReport` with counts, duration, success flag

## Separation from Execution

The compiler:

- Does **not** access repositories
- Does **not** access databases
- Does **not** modify application state
- Does **not** use Spring annotations

`BootstrapCompilationRunner` (Spring) invokes the pure compiler at startup and logs statistics. Execution belongs to a future prompt.

## Startup Log

```text
Bootstrap Compilation

Commands Generated

Canon ............. 1
World Rules ....... 1
Characters ........ 13
...
Total Commands .... 28

Compilation Time . 2 ms

Warnings . 0
```
