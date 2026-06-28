# Prompt Composer

The Prompt Composer transforms a `ResolvedContext` into a structured, provider-independent prompt representation. It decides **structure**, not size, format, or LLM invocation.

## Philosophy

| Stage | Responsibility |
|-------|----------------|
| Planner | Decides **what** context to load |
| Resolver | Retrieves **content** |
| Composer | Decides **structure** |
| Budget Allocator *(future)* | Decides **size** |
| LLM Adapter *(future)* | Decides **format** |
| LLM | Decides **answer** |

The composer never concatenates everything into one string, never allocates token budgets, never calls an LLM, and never knows about OpenAI, Gemini, Claude, Ollama, or other providers.

## Input

`PromptComposeRequest` carries:

| Field | Description |
|-------|-------------|
| `resolvedContext` | Output from the Context Resolver |
| `latestUserMessage` | The user's latest message |
| `currentCharacter` | Logged-in character metadata |
| `session` | Active chat session |
| `conversation` | Current conversation (may be null) |

The developer endpoint resolves context automatically before composing.

## Output

`ComposedPrompt` contains ordered `PromptSection` records — **not** a single prompt string.

Each `PromptSection` is immutable:

| Field | Description |
|-------|-------------|
| `sectionType` | Semantic category (see below) |
| `title` | Human-readable section label |
| `priority` | Lower number = earlier in composition |
| `required` | Whether the section is structurally required |
| `estimatedTokens` | Rough estimate from content length ÷ 4 |
| `content` | Section text |

`ComposedPrompt` also exposes:

- `totalEstimatedTokens()`
- `requiredSections()`
- `optionalSections()`

## Section Types

| Type | Purpose |
|------|---------|
| `SYSTEM_IDENTITY` | Logged-in character identity (from session) |
| `PERSONALITY` | Bandar voice / prompt profile |
| `WORLD_FACTS` | Canon and world state (merged) |
| `CURRENT_CHARACTER` | Active character profile |
| `CURRENT_LOCATION` | Where the character is |
| `RELATIONSHIPS` | Relevant relationships |
| `RELEVANT_STORIES` | Story context |
| `CURRENT_CONVERSATION` | Recent conversation turns |
| `SESSION_SUMMARY` | Session-level summary |
| `PUBLIC_EVENTS` | Public world events |
| `LONG_TERM_MEMORY` | Retrieved memories |
| `SECRET_MEMORY` | Restricted memories (if permitted) |
| `USER_MESSAGE` | Latest user message |
| `INSTRUCTIONS` | Behavioral instructions (always last) |
| `UNKNOWN` | Unmapped resolved context |

## Composition Order

Sections are ordered by priority:

1. `SYSTEM_IDENTITY`
2. `PERSONALITY`
3. `WORLD_FACTS`
4. Current world state (`CURRENT_CHARACTER`, `CURRENT_LOCATION`, `RELATIONSHIPS`, `RELEVANT_STORIES`, `PUBLIC_EVENTS`)
5. Conversation (`CURRENT_CONVERSATION`, `SESSION_SUMMARY`, `LONG_TERM_MEMORY`, `SECRET_MEMORY`)
6. `USER_MESSAGE`
7. `INSTRUCTIONS` *(last)*

Default instruction appended to every composition:

> Answer only using the provided world knowledge. If uncertain, admit uncertainty rather than inventing facts.

`PROMPT_RULES` from resolved context are prepended to the instructions section.

## Required vs Optional

Always **required**:

- `SYSTEM_IDENTITY`
- `PERSONALITY`
- `WORLD_FACTS` (when canon/state is present)
- `CURRENT_CONVERSATION` (when present in resolved context)
- `USER_MESSAGE`
- `INSTRUCTIONS`

Keyword-selected sections (location, stories, memory, etc.) are **optional**.

## Prompt Inspection

`PromptInspection` is a developer summary of a `ComposedPrompt`:

- Section order
- Estimated tokens per section
- Required vs optional flags
- Total token estimate

No provider formatting is included.

## Developer API

```
POST /api/prompt/compose
```

**Request**

```json
{ "latestMessage": "Where am I in the Jungle?" }
```

**Response**

```json
{
  "sections": [
    {
      "sectionType": "SYSTEM_IDENTITY",
      "title": "System Identity",
      "priority": 5,
      "required": true,
      "estimatedTokens": 15,
      "content": "Character ID: character_hippu_king\n..."
    }
  ],
  "totalEstimatedTokens": 120,
  "requiredSectionCount": 5,
  "optionalSectionCount": 2,
  "inspection": { "...": "..." }
}
```

Requires an active session (same as context endpoints). Developer-oriented; not used by the chat UI in MVP0.

## Frontend

The **Developer Panel** (`/dev`) includes a **Prompt Composition** tab that displays:

- Ordered sections with titles and types
- Required vs optional flags
- Estimated tokens per section
- Section content (structured, not a final provider prompt)
- Total estimated tokens

## Future Work

### Budget Allocation

A future Budget Allocator will trim or summarize optional sections to fit model limits. The composer produces the full semantic structure; budgeting happens downstream.

### Provider Adapters

Future LLM Adapters will map `ComposedPrompt` sections to provider-specific message formats (system/user/assistant roles, XML tags, etc.). The composer remains provider-agnostic.

## Related Docs

- [Context Planner](context-planner.md)
- [Context Resolution](context-resolution.md)
