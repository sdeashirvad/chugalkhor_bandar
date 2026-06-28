# Groq LLM Provider

Chugalkhor Bandar can generate Bandar replies through [Groq](https://groq.com/) using their OpenAI-compatible Chat Completions API. The Groq provider is the only probabilistic step in the pipeline; everything upstream (planner, knowledge fragments, prompt composition, context profile, budget allocation) remains deterministic.

## Configuration

Set environment variables in `backend/.env` (loaded automatically at startup):

| Variable | Purpose | Default |
|----------|---------|---------|
| `LLM_PROVIDER` | Active provider (`mock` or `groq`) | `mock` |
| `GROQ_API_KEY_1` | Primary Groq API key | — |
| `GROQ_API_KEY_2` | Secondary Groq API key (optional) | — |
| `GROQ_MODEL` | Groq model id (also sets `llm.model`) | `mock-bandar` when mock, otherwise set explicitly |
| `GROQ_BASE_URL` | Groq API base URL | `https://api.groq.com/openai/v1` |
| `LLM_TEMPERATURE` | Sampling temperature | `0.7` |
| `LLM_MAX_OUTPUT_TOKENS` | Max completion tokens | `1024` |
| `LLM_TIMEOUT_SECONDS` | HTTP timeout for Groq requests | `60` |

### Example `.env` for Groq

```
LLM_PROVIDER=groq
GROQ_API_KEY_1=gsk_...
GROQ_API_KEY_2=gsk_...
GROQ_MODEL=llama-3.3-70b-versatile
LLM_TEMPERATURE=0.7
LLM_MAX_OUTPUT_TOKENS=1024
LLM_TIMEOUT_SECONDS=60
```

API keys are never exposed through APIs or the developer panel. The panel shows only the key **index** (1 or 2).

## Switching providers

Change `LLM_PROVIDER` only — no code changes required:

- `LLM_PROVIDER=mock` — developer inspection replies (no external API)
- `LLM_PROVIDER=groq` — real Groq-backed generation

Restart the backend after changing `.env`.

## Key rotation

When both `GROQ_API_KEY_1` and `GROQ_API_KEY_2` are configured, requests rotate in round-robin order:

1. Request 1 → Key 1  
2. Request 2 → Key 2  
3. Request 3 → Key 1  
4. …

Rotation is deterministic and thread-safe. Blank keys are ignored; a single key disables rotation but still works.

## Retry strategy

If a Groq request fails with a **retryable** error (HTTP 429 rate limit/quota, or 5xx temporary failure), the provider retries **once** using the alternate key.

If all keys fail (or only one key is configured and it fails), the API returns a mapped `ProviderException` message — raw HTTP errors are never sent to the frontend.

## Pipeline integration

Both the developer **LLM Request** tab and the player **Chat** page use the same path:

```
Conversation → Planner → Knowledge Fragments → Prompt Composer
→ Context Profile → Budget Allocation → Provider Request → Groq Provider → Reply
```

The Groq provider receives only a `ProviderRequest` (messages, temperature, max tokens, model). It has no knowledge of bootstrap data, fragments, or prompt composition.

## Health

`GroqProvider.health()` reports whether at least one API key is loaded. Provider info includes the active model and loaded key count. Secrets are never included.

## Adding additional keys

The key pool is designed for future expansion beyond two keys. Today, configure `GROQ_API_KEY_1` and `GROQ_API_KEY_2`. Additional keys would require extending `GroqProperties` and the pool loader.

## Developer panel diagnostics

The **LLM Request** tab displays:

- Active provider
- Selected API key index (1 or 2)
- Model, latency, prompt/completion tokens
- Finish reason and retry count
- Response preview

Request logging to the server log occurs only in the `dev` Spring profile.

## Switching back to Mock provider

```
LLM_PROVIDER=mock
```

The mock provider remains fully available for local development and automated tests without Groq credentials.
