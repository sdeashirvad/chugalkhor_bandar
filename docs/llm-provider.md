# LLM Provider Abstraction

The LLM layer converts a provider-independent `ComposedPrompt` into a provider-specific request and executes it through a pluggable provider. Initially only the **Mock Provider** is implemented — no external API calls.

## Philosophy

| Layer | Responsibility |
|-------|----------------|
| Prompt Composer | Creates **meaning** (structured sections) |
| Provider Adapter | Creates **provider-specific requests** |
| Provider | **Executes** the request |

This separation lets the Jungle switch providers (Gemini, OpenAI, Claude, Ollama, OpenRouter) without changing business logic upstream.

## Package

`application/llm/`

## Provider Interface

`LLMProvider` defines:

| Method | Purpose |
|--------|---------|
| `generateReply(ProviderRequest)` | Execute the request and return a response |
| `health()` | Whether the provider is reachable / configured |
| `providerInfo()` | Name, type, model, description |

## Provider Request

Immutable `ProviderRequest` contains:

| Field | Description |
|-------|-------------|
| `messages` | Ordered provider messages (system / user / assistant) |
| `metadata` | Adapter-level metadata (section counts, token estimates) |
| `temperature` | Sampling temperature |
| `maxOutputTokens` | Output token limit |
| `model` | Model identifier |

No provider-specific SDK types appear in this layer.

## Provider Response

`ProviderResponse` contains:

| Field | Description |
|-------|-------------|
| `reply` | Generated text (inspection text for mock) |
| `tokenUsage` | Prompt / completion / total tokens (estimated for mock) |
| `providerMetadata` | Provider-specific metadata map |
| `latencyMs` | Round-trip latency |
| `finishReason` | Completion reason (e.g. `stop`) |

## Provider Adapter

`PromptToProviderAdapter` converts `ComposedPrompt` → `ProviderRequest`.

It does **not** concatenate everything into one blob. Instead:

- Context sections (`PERSONALITY`, `WORLD_FACTS`, `CURRENT_CHARACTER`, etc.) become **system** messages with section titles.
- `USER_MESSAGE` becomes a **user** message.
- `INSTRUCTIONS` becomes a **system** message (after the user turn, matching composer order).
- `CURRENT_CONVERSATION` history is parsed into **user** / **assistant** turns when lines follow `USER:` / `BANDAR:` / `SYSTEM:` prefixes.

## Mock Provider

`MockLLMProvider` receives a `ProviderRequest` and returns a developer-friendly inspection reply:

```text
[Mock Bandar]

I have received your request.

Sections:

- Personality
- World Facts
- Current Character
- Conversation

User asked:

"Where am I?"
```

No AI. No generation. Pure inspection.

Token usage is estimated from character counts (÷ 4).

## LLM Service

`LLMService` orchestrates the pipeline:

1. Compose prompt (`PromptComposeService`)
2. Adapt to provider request (`PromptToProviderAdapter`)
3. Execute via configured provider (`LLMProviderRegistry`)

Returns `LLMGenerateResult` with provider info, request, and response.

## Provider Registry

`LLMProviderRegistry` selects the active provider from configuration.

| Type | Status |
|------|--------|
| `MOCK` | Implemented |
| `GEMINI` | Planned |
| `OPENAI` | Planned |
| `CLAUDE` | Planned |
| `OLLAMA` | Planned |
| `OPENROUTER` | Planned |

Selecting an unimplemented provider throws `UnsupportedOperationException`.

## Configuration

`application.yml`:

```yaml
llm:
  provider: mock
  model: mock-bandar
  temperature: 0.7
  max-output-tokens: 1024
```

## Developer API

```
POST /api/llm/generate
```

**Request**

```json
{ "latestMessage": "Where am I?" }
```

**Response**

```json
{
  "provider": {
    "type": "MOCK",
    "name": "Mock Bandar",
    "description": "Developer inspection provider — no AI generation",
    "healthy": true,
    "model": "mock-bandar"
  },
  "request": {
    "messages": [
      { "role": "SYSTEM", "content": "Personality\n\n...", "sectionType": "PERSONALITY", "metadata": {} }
    ],
    "metadata": { "sectionCount": "5", "estimatedPromptTokens": "120" },
    "temperature": 0.7,
    "maxOutputTokens": 1024,
    "model": "mock-bandar"
  },
  "response": {
    "reply": "[Mock Bandar]\n\n...",
    "tokenUsage": { "promptTokens": 30, "completionTokens": 20, "totalTokens": 50 },
    "providerMetadata": { "provider": "mock", "model": "mock-bandar" },
    "latencyMs": 1,
    "finishReason": "stop"
  }
}
```

Requires an active session (same as context and prompt endpoints).

### Full Pipeline

```
Conversation
  ↓
Planner
  ↓
Resolver
  ↓
Composer
  ↓
Provider Adapter
  ↓
Mock Provider
  ↓
Provider Response
```

## Frontend

The **Developer Panel** (`/dev`) includes an **LLM Request** tab showing:

- Active provider
- Provider request messages and metadata
- Mock response (reply, token usage, latency, finish reason)

## Out of Scope (MVP)

- Real LLM API calls
- API keys and secrets
- Token budgeting
- Memory extraction

## Related Docs

- [Prompt Composer](prompt-composer.md)
- [Context Resolution](context-resolution.md)
