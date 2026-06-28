# Context Profiles

Context profiles tailor prompt composition and budget allocation to the user's intent. They sit between semantic composition and token budgeting.

## Philosophy

| Stage | Responsibility |
|-------|----------------|
| Planner | Decides **what** context to load |
| Resolver | Retrieves **content** |
| Composer | Creates **sections** |
| Profile Selector | Decides **importance** |
| Budget Allocator | Decides **size** |
| Provider Adapter | Decides **format** |
| LLM | Decides **answer** |

Profiles do not call LLMs, summarize content, or extract memory.

## Package

`application/prompt/profile/`

## Context Profile

Immutable `ContextProfile` defines:

| Field | Description |
|-------|-------------|
| `type` | Profile identifier |
| `displayName` | Human-readable label |
| `description` | Profile purpose |
| `preferredSections` | Sections boosted during budgeting |
| `optionalSections` | Sections that may be dropped |
| `minimumRequiredSections` | Sections that must survive budgeting |
| `reducedSections` | Sections deprioritized or dropped first |
| `sectionPriorities` | Profile-specific ordering overrides |

## Implemented Profiles

| Profile | Trigger (examples) | Prioritize | Reduce |
|---------|-------------------|------------|--------|
| `GENERAL_CHAT` | Default fallback | Personality, conversation, world facts | — |
| `LOCATION_QUERY` | `"where"` | Current location, character, conversation | World facts, stories |
| `CHARACTER_QUERY` | `"who"` | Character, relationships, conversation | Stories, public events |
| `STORY_QUERY` | `"story"` | Stories, world facts, conversation | Current location |
| `WORLD_QUERY` | `"world"`, `"jungle"`, `"canon"` | World facts, public events, conversation | Location, relationships |
| `MEMORY_QUERY` | `"remember"` | Long-term memory, session summary, conversation | Stories, public events |
| `RELATIONSHIP_QUERY` | `"king"`, `"relationship"`, planner trace | Relationships, character, conversation | Stories, memory |
| `UNKNOWN` | Blank message or empty context | Conversation only | Most optional context |

## Profile Selector

`ContextProfileSelector` uses deterministic keyword and trace rules only:

1. Analyze latest user message (normalized lowercase)
2. Consider planner trace entries when relevant
3. Consider whether resolved context is empty
4. Select exactly one profile with a human-readable reason

No ML. No embeddings. No learning.

## Configuration

Profile priority overrides can be supplied in `application.yml`:

```yaml
prompt:
  profiles:
    location-query:
      current_location: 55
      current_character: 45
    story-query:
      relevant_stories: 95
      current_location: 200
```

Keys use kebab-case profile names and snake-case section types (mapped to `PromptSectionType`).

## Developer API

```
POST /api/prompt/profile
```

**Request**

```json
{ "latestMessage": "Where am I?" }
```

**Response**

```json
{
  "profile": {
    "type": "LOCATION_QUERY",
    "displayName": "Location Query",
    "description": "Prioritize place and character context for location questions.",
    "preferredSections": ["CURRENT_LOCATION", "CURRENT_CHARACTER", "CURRENT_CONVERSATION"],
    "reducedSections": ["WORLD_FACTS", "RELEVANT_STORIES"],
    "minimumRequiredSections": ["SYSTEM_IDENTITY", "PERSONALITY", "..."],
    "optionalSections": ["..."],
    "sectionPriorities": { "CURRENT_LOCATION": 55 }
  },
  "selectionReason": "User message contains \"where\""
}
```

Requires an active session.

## Frontend

The **Developer Panel** includes a **Context Profile** tab showing:

- Selected profile type and description
- Selection reason
- Preferred and reduced sections

## Future Work

### Adaptive Profiles

Future versions may learn profile weights from usage patterns. MVP0 uses fixed rules and configuration overrides only.

### Summarization Integration

Profiles will inform which sections receive summarization when budgets are tight. MVP0 drops or truncates instead.

## Related Docs

- [Budget Allocation](budget-allocation.md)
- [Prompt Composer](prompt-composer.md)
