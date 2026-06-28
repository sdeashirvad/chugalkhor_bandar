# Context Resolution

The Context Resolution Engine turns semantic **references** from a `ContextPlan` into actual **context payloads**. It does not build prompts or call an LLM.

## Pipeline

```
Latest message → Context Planner → ContextPlan (references)
                                      ↓
                               Context Resolver → ResolvedContext (content)
                                      ↓
                            (future) Prompt Builder → prompt text
                                      ↓
                            (future) LLM → answer
```

| Stage | Decides |
|-------|---------|
| **Planner** | WHAT information is needed |
| **Resolver** | WHERE to load it from (and loads it) |
| **Prompt Builder** (future) | HOW to phrase it |
| **LLM** (future) | ANSWER |

## ContextReference

Immutable structured pointer:

| Field | Example |
|-------|---------|
| `provider` | `characters`, `canon`, `conversationEngine` |
| `entityType` | `character`, `canon`, `conversation` |
| `entityId` | `character_alpha`, `canon_main` |
| `attribute` | `profile`, `sections`, `window` |
| `priority` | Section ordering hint |

The string form (`character:character_alpha:profile`) is for display only.

## ResolvedContext

Contains ordered `ResolvedContextSection` entries:

| Field | Description |
|-------|-------------|
| Section metadata | type, priority, source, reference |
| `content` | Resolved payload text (not prompt text) |
| `estimatedTokens` | Rough estimate from content length ÷ 4 |

## ContextResolver

1. Iterates sections in the `ContextPlan`
2. Runs stub **permission checks** (e.g. denies `SECRET_MEMORY`)
3. Routes each section to the matching provider's `resolve()` method
4. Returns missing-entity placeholders when data is not found
5. Preserves priority ordering

## Provider Contract

Each context provider exposes:

| Method | Used by |
|--------|---------|
| `plan()` | Context Planner — returns `ContextSection` with references |
| `resolve()` | Context Resolver — returns `ResolvedContextSection` with content |

Providers do not call each other. The resolver orchestrates them.

## Permission Checks (Stub)

`StubContextPermissionChecker` denies access to `SECRET_MEMORY` sections and returns `[access denied]` without loading content. Future phases can enforce character-specific secrets, role rules, and family visibility.

## API

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/api/context/plan` | Plan only (references) |
| `POST` | `/api/context/resolve` | Plan + resolve (content payloads) |

Both require a valid session. Body: `{ "latestMessage": "..." }`.

## Developer Panel

**Developer → Resolved Context** tab at `/dev`:

- Enter a sample user message
- View resolved section content, references, and token estimates

## Future: RAG, Cache, Permissions

1. **RAG** — Replace keyword planning with retrieval; resolver loads chunks by embedding match while keeping the same reference model.
2. **Cache** — Cache resolved sections by `(reference, world version)` to avoid repeated repository reads.
3. **Permissions** — Real secrecy rules before resolve; filter character `secrets` sections based on relationship and story context.
4. **Prompt Builder** — Consume `ResolvedContext` and format sections into LLM messages (separate subsystem).

The resolver stays storage-aware but prompt-agnostic.
