# Behavior Engine

The Behavior Engine selects Bandar's **conversational style** for the current interaction.

It does not generate language. It does not modify facts. It does not influence world knowledge. It only decides *how* Bandar should express himself.

The LLM remains responsible for natural language generation.

## Personality vs Behavior

| Layer | Meaning | Changes? |
| --- | --- | --- |
| **Personality** | Who Bandar is — wise, kind, curious, ancient | Constant |
| **Behavior** | How Bandar behaves in *this* conversation | Per interaction |

Example: Bandar's personality is always kind. In one conversation he may feel playful; in another, reflective; during a festival, storytelling; during a serious question, calm and direct.

## BehaviorProfile

An immutable record of **decisions only** — never generated text.

| Field | Meaning |
| --- | --- |
| `openingStyle` | How to begin the reply |
| `narrationStyle` | One active narration mode |
| `humorLevel` | `OFF`, `LIGHT`, or `MEDIUM` — never sharp or sarcastic |
| `curiosityLevel` | How naturally Bandar may ask follow-up questions |
| `endingStyle` | How to close the reply |
| `conversationFlavor` | Overall feel of the exchange |
| `energyModifier` | Subtle pacing energy adjustment |
| `storytellingPreference` | How strongly to lean into narrative |
| `createdAt` | When the profile was selected |

## Deterministic Selection

`BehaviorEngine` receives:

1. Current User
2. Working Memory
3. Conversation Director plan
4. Conversation window
5. Runtime world

It applies ordered, deterministic rules — no LLM involved.

Example rules:

| Director goal | Narration | Flavor | Humor |
| --- | --- | --- | --- |
| `STORY` | `STORY` | `NOSTALGIC` or `ADVENTUROUS` | `LIGHT` |
| `LOCATION_HELP` | `DIRECT` | `CALM` | `OFF` or `LIGHT` |
| `CHEER_UP` | `PLAYFUL` | `COZY` | `MEDIUM` |
| `GOODBYE` | `DIRECT` | `CALM` | `OFF` |

Each match records a **planning trace** explaining which rules fired.

## Execution Flow

```text
User message
    ↓
Working Memory rebuild
    ↓
Conversation Director.plan()
    ↓
BehaviorEngine.select()
    ↓
ConversationPlanExecutor (activates BehaviorContext)
    ↓
PromptComposer adds Conversation Style section
    ↓
LLM generates reply text
```

## Prompt Integration

The Prompt Composer adds a **Conversation Style** section immediately before Instructions:

```text
Conversation Style

Today you feel calm and curious.

Open with a gentle observation.

Prefer historical storytelling.

Use light humor only if it feels natural.

End with a thoughtful question.
```

`BehaviorInstructionBuilder` translates the profile into natural guidance. Internal enum names are never exposed to the LLM.

Behavior instructions contain **no world facts** — only stylistic guidance.

## Developer API

| Endpoint | Description |
| --- | --- |
| `GET /api/behavior/current` | Return the current BehaviorProfile for the active session |

The Developer Panel **Behavior Engine** tab shows conversation flavor, opening/narration/ending styles, humor and curiosity levels, planning trace, and applied rules.

## Future Integration

Later prompts will add:

- **Greeting Engine** — may influence opening style selection
- **Conversation Artifacts** — durable records that may inform future behavior rules

The Behavior Engine remains the ephemeral style layer: tactical expression choices separate from personality, world knowledge, and durable conversation records.

## Package Layout

```text
application/behavior/
  BehaviorProfile.java
  BehaviorEngine.java
  BehaviorEngineService.java
  BehaviorInstructionBuilder.java
  BehaviorContext.java
  InMemoryBehaviorProfileStore.java
```
