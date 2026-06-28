# Conversation Director

The Conversation Director is Bandar's **executive brain**. It decides how the conversation should progress.

It never generates language. It never calls the LLM directly. It only produces a deterministic **ConversationPlan** that the existing LLM pipeline executes.

## Separation of Responsibilities

| Component | Decides |
| --- | --- |
| **Conversation Director** | What should Bandar do next? |
| **LLM** | How should Bandar say this? |

These responsibilities remain completely separate.

## Conversation Energy

Instead of fixing a message count at planning time, the Director assigns **ConversationEnergy** — how much conversational momentum the interaction deserves.

| Energy | Meaning | Typical message count |
| --- | --- | --- |
| `LOW` | Greetings, location, identity, simple facts | Exactly 1 |
| `MEDIUM` | Slightly richer exchanges | Usually 1, occasionally 2 |
| `HIGH` | Stories, boredom, festivals | Normally 2, occasionally 3 |
| `VERY_HIGH` | Long stories, deep historical discussion | Up to 3 (never more) |

The executor **derives** the final message count deterministically from energy, session id, and user message.

## Conversation Arc

Each plan includes a **ConversationArc** describing the intended flow:

| Arc | Purpose |
| --- | --- |
| `QUESTION_ANSWER` | Direct question and reply |
| `QUESTION_STORY` | Story request unfolding over replies |
| `GREETING_REPLY` | Welcome and rapport |
| `CHEER_UP` | Lift mood with story or play |
| `REMINDER` | Acknowledge remember/remind requests |
| `GOODBYE` | Graceful close |
| `SMALL_TALK` | Light conversational exchange |
| `STORY_CONTINUATION` | Resume an in-progress narrative |

The arc is deterministic and drives pacing instructions sent to the LLM.

## ConversationPlan

An immutable plan containing **decisions only** — never generated text.

| Field | Meaning |
| --- | --- |
| `goal` | Why Bandar is replying |
| `confidence` | How strongly the matched rule applies (0–1) |
| `continueConversation` | Whether Bandar should keep the exchange open |
| `conversationEnergy` | Conversational momentum level |
| `conversationArc` | Intended narrative flow |
| `expectedMessageCount` | Derived reply count (1–3) |
| `delays` | Milliseconds between sequential replies |
| `askFollowUpQuestion` | Whether a follow-up question is appropriate |
| `tellStory` | Prefer storytelling |
| `tellJoke` | Prefer humour |
| `tellMemory` | Refer to conversational memory |
| `endConversation` | Wind down gracefully |
| `suggestedTone` | Conversational tone hint for the LLM |
| `outcome` | Expected result of executing the plan |
| `createdAt` | When the plan was created |
| `isInterrupted` | Execution was cut short |
| `isCancelled` | Pending replies were cancelled |
| `startedAt` | When execution began |
| `completedAt` | When execution finished |

## Follow-up Behaviour

Follow-up questions are not used merely to extend the conversation. They are chosen deterministically by arc:

| Context | Follow-up frequency |
| --- | --- |
| Location | Occasionally |
| Story | Often |
| Identity | Rarely |
| Goodbye | Never |

## Natural Pacing

Replies are not sent simultaneously. The executor waits between messages using realistic typing delays:

| Gap | Default range |
| --- | --- |
| Message 2 after message 1 | 2–4 seconds |
| Message 3 after message 2 | 3–6 seconds |

In development, `dev-delay-multiplier` (default `0.25`) scales delays down while preserving relative pacing.

Story arcs split at natural narrative boundaries:

1. Brief introduction
2. Main story body
3. Reflection or gentle follow-up

## Execution Lifecycle

```text
User message
    ↓
Cancel any pending replies from previous plan
    ↓
Working Memory rebuild
    ↓
ConversationDirector.plan()  → energy + arc + derived count
    ↓
ConversationPlanExecutor
    ↓
Reply 1 → delay → Reply 2 → delay → Reply 3 (max 3)
    ↓
Messages persisted + timeline recorded
```

### Cancellation

If a new user message arrives before all planned replies have been delivered:

1. Every remaining scheduled reply is cancelled
2. Working memory is rebuilt
3. A fresh plan is created from the latest user message
4. Old plans never continue after the conversation has moved on

During each LLM call, `ConversationPlanContext` activates the plan so the prompt includes director instructions such as:

```text
Conversation Goal

Tell a story.
Begin with a brief introduction — set the scene lightly.
This is reply 1 of 3.
```

The Director never generates reply text — it only influences the prompt.

## Configuration

```yaml
chugalkhor:
  conversation-director:
    max-messages: 3
    dev-delay-multiplier: 0.25
    conversation-energy-thresholds:
      medium-second-message-modulo: 3
      high-third-message-modulo: 4
    message-delay-range:
      second-message-min-ms: 2000
      second-message-max-ms: 4000
      third-message-min-ms: 3000
      third-message-max-ms: 6000
```

In the `dev` profile, `dev-delay-multiplier: 0.25` keeps pacing pleasant during local testing.

## Developer API

| Endpoint | Description |
| --- | --- |
| `GET /api/conversation/director/current-plan` | Return the most recent ConversationPlan for the active session |

The Developer Panel **Conversation Director** tab shows:

- Conversation Energy and Arc
- Derived message count
- Execution timeline
- Delivered and cancelled messages
- Interruption reason
- Planning trace and execution status

## Future Integration With Conversation Artifacts

Later prompts will introduce **Conversation Artifacts** — durable records of notable exchanges.

The Conversation Director will remain the ephemeral decision layer:

- Artifacts may inform future planning rules.
- The Director will not write artifacts directly.
- Outcomes like `STORY_STARTED` or `PROMISE_MADE` will eventually feed artifact and memory systems.

That keeps tactical reply strategy separate from durable conversation records.

## Package Layout

```text
application/conversation/director/
  ConversationEnergy.java
  ConversationArc.java
  ConversationGoal.java
  ConversationOutcome.java
  ConversationPlan.java
  ConversationDirector.java
  ConversationDirectorService.java
  ConversationPlanExecutor.java
  ConversationExecutionRegistry.java
  ConversationMessageCountDeriver.java
  ConversationDelayDeriver.java
  ConversationPlanInstructionBuilder.java
  ConversationPlanContext.java
```
