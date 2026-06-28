# Cognitive Analysis

The Cognitive Analysis Engine performs **asynchronous semantic analysis** of completed conversations.

It does not generate replies, modify the world, or create memories directly. It only produces structured observations and recommendations.

## Philosophy

Deterministic systems own the world. LLMs help understand conversations.

The Cognitive Analysis Engine may **suggest**. Only deterministic systems **decide**.

## Purpose

| Layer | Role |
| --- | --- |
| Conversation Director | Plans the next reply |
| Conversation Artifacts | Track unfinished intentions |
| Cognitive Analysis | Observe and recommend |
| Memory Inbox (future) | Promote facts deterministically |

## Provider Abstraction

`CognitiveAnalysisProvider` is independent from reply generation.

| Provider | Description |
| --- | --- |
| `mock` | Deterministic fixture JSON for development |
| `groq` | Groq chat completions with low temperature, separate HTTP client and timeout |

The reply `LLMService` pipeline is **not** reused. Analysis builds its own prompt and calls the cognitive provider directly.

## Output Model

### CognitiveAnalysisResult

| Field | Meaning |
| --- | --- |
| `analysisId` | Unique analysis record |
| `conversationId` | Source conversation |
| `provider` / `model` | Provider metadata |
| `latencyMs` | Provider latency |
| `confidence` | Aggregate confidence |
| `observations` | Descriptive findings |
| `recommendations` | Advisory actions |
| `rawJson` | Original provider JSON |

### Observation Types

`PROMISE`, `REMINDER`, `PREFERENCE`, `RELATIONSHIP_SIGNAL`, `STORY_SEED`, `OPEN_QUESTION`, `GOSSIP`, `FACT_CANDIDATE`, `EMOTION`, `INTEREST`, `UNKNOWN`

Observations are descriptive. They never mutate the world.

### Recommendation Actions

`PROMOTE_TO_MEMORY`, `CREATE_NOTIFICATION`, `MERGE_ARTIFACT`, `IGNORE`, `WAIT`

Recommendations are advisory only.

## Execution Flow

```text
ConversationService.appendUserMessage()
    → Director + Executor deliver reply
    → Artifacts generated
    → CognitiveAnalysisTrigger.schedule() [async]
        → Gather transcript, artifacts, working memory, director outcome
        → CognitiveAnalysisProvider.analyzeConversation()
        → Parse JSON → persist CognitiveAnalysisResult
```

Analysis failure never affects the user conversation. Failures are logged and persisted as diagnostics.

## Configuration

```yaml
chugalkhor:
  cognitive-analysis:
    enabled: true
    provider: mock
    temperature: 0.1
    timeout-seconds: 60
    minimum-confidence: 0.5
    run-asynchronously: true
    mock-enabled: true
```

Set `enabled: false` to disable analysis entirely.

## API

| Endpoint | Description |
| --- | --- |
| `GET /api/cognition/latest` | Latest analysis for current character |
| `GET /api/cognition/{conversationId}` | Analysis for a conversation |
| `GET /api/cognition/observations` | Flattened observations |
| `GET /api/cognition/recommendations` | Flattened recommendations |
| `GET /api/cognition/dev/execution` | Latest execution snapshot |
| `GET /api/cognition/dev/all` | All stored analyses |

## Future Integration

Memory Inbox, nightly compression, and chronicle generation will consume cognitive analysis outputs. No automatic promotion or notification creation occurs in this prompt.

## Package Layout

```text
application/cognition/
  CognitiveAnalysisEngine.java
  CognitiveAnalysisService.java
  CognitiveAnalysisProvider.java
  MockCognitiveAnalysisProvider.java
  GroqCognitiveAnalysisProvider.java
  CognitiveAnalysisJsonParser.java
```
