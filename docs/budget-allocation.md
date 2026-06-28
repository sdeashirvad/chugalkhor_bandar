# Budget Allocation

The Budget Allocation Engine transforms a generic `ComposedPrompt` into a `BudgetedPrompt` sized for the selected context profile and target provider.

## Philosophy

Composition creates **structure**. Profiles decide **importance**. Budgeting decides **size**. The provider adapter formats the result — no LLM is called at this stage.

## Package

`application/prompt/budget/`

## Budget Model

### SectionBudget

| Field | Description |
|-------|-------------|
| `sectionType` | Prompt section type |
| `maxTokens` | Maximum tokens allocated |
| `minimumTokens` | Floor for required sections |
| `priority` | Profile-adjusted ordering |
| `required` | Whether the section must be kept |

### PromptBudget

Contains all section budgets plus provider-level totals:

| Field | Description |
|-------|-------------|
| `sectionBudgets` | Per-section allocations |
| `totalAvailableTokens` | Prompt budget (`maxContext − reservedOutput`) |
| `reservedOutputTokens` | Tokens reserved for model output |
| `maxContextTokens` | Provider context window |

## Budget Allocator

`BudgetAllocator` input:

- `ComposedPrompt`
- `ContextProfile`
- `ProviderCapabilities`

Output: `BudgetedPrompt`

### Responsibilities

1. Assign token budgets weighted by profile preferences
2. Drop optional sections first (reduced sections dropped before other optional sections)
3. Never remove required sections
4. Preserve original section ordering in the kept result
5. Truncate section content when over budget (simple character-based truncation for MVP0)

No summarization. No semantic compression.

## Provider Capabilities

Immutable `ProviderCapabilities`:

| Field | Description |
|-------|-------------|
| `maxContextTokens` | Total context window |
| `reservedOutputTokens` | Output reservation |
| `supportsSystemMessages` | System role support |
| `supportsMultiMessage` | Multi-turn message support |

`availablePromptTokens()` = `maxContextTokens − reservedOutputTokens`

The mock provider exposes:

- `maxContextTokens`: 8192
- `reservedOutputTokens`: from `llm.max-output-tokens` (default 1024)

Future providers override these values.

## Budget Rules (by Profile)

### LOCATION_QUERY

Prioritize: Current Location, Current Character, Conversation  
Reduce: World Facts, Stories

### STORY_QUERY

Prioritize: Stories, World Facts, Conversation  
Reduce: Current Location

### GENERAL_CHAT

Balanced weights across present sections.

## Developer API

```
POST /api/prompt/budget
```

**Request**

```json
{ "latestMessage": "Where am I?" }
```

**Response**

```json
{
  "profile": { "type": "LOCATION_QUERY", "...": "..." },
  "selectionReason": "User message contains \"where\"",
  "sections": [
    {
      "section": { "sectionType": "CURRENT_LOCATION", "content": "...", "...": "..." },
      "budget": { "sectionType": "CURRENT_LOCATION", "maxTokens": 120, "minimumTokens": 16, "priority": 55, "required": false },
      "truncated": false,
      "allocatedTokens": 45
    }
  ],
  "droppedSections": [
    {
      "sectionType": "RELEVANT_STORIES",
      "title": "Relevant Stories",
      "estimatedTokens": 200,
      "reason": "Reduced by profile LOCATION_QUERY"
    }
  ],
  "budget": {
    "sectionBudgets": ["..."],
    "totalAvailableTokens": 7168,
    "reservedOutputTokens": 1024,
    "maxContextTokens": 8192
  },
  "totalPromptTokens": 380,
  "remainingBudget": 6788,
  "providerCapabilities": {
    "maxContextTokens": 8192,
    "reservedOutputTokens": 1024,
    "availablePromptTokens": 7168,
    "supportsSystemMessages": true,
    "supportsMultiMessage": true
  }
}
```

Pipeline for this endpoint:

```
Compose → Profile Select → Budget Allocate
```

Requires an active session.

## Frontend

The **Developer Panel** includes a **Budget Allocation** tab showing:

- Selected profile
- Section budgets (max, min, allocated, required, truncated)
- Dropped sections with reasons
- Total prompt tokens and remaining budget

## Configuration

```yaml
prompt:
  budget:
    minimum-section-tokens: 16
```

## Future Work

### Summarization

When budgets are tight, optional sections will be summarized instead of truncated. MVP0 uses drop + truncate only.

### Provider-Specific Strategies

Different providers may require different message packing strategies after budgeting. That remains the adapter's job downstream.

## Related Docs

- [Context Profiles](context-profiles.md)
- [LLM Provider](llm-provider.md)
- [Prompt Composer](prompt-composer.md)
