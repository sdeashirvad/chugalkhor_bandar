CREATE TABLE cognitive_analyses (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    character_id VARCHAR(255) NOT NULL,
    conversation_id VARCHAR(255) NOT NULL,
    provider VARCHAR(64) NOT NULL,
    model VARCHAR(128) NOT NULL,
    latency_ms BIGINT NOT NULL,
    confidence DOUBLE PRECISION NOT NULL,
    created_at TIMESTAMP NOT NULL,
    raw_json TEXT NOT NULL,
    observations_json TEXT NOT NULL,
    recommendations_json TEXT NOT NULL
);

CREATE TABLE cognitive_analysis_diagnostics (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    character_id VARCHAR(255) NOT NULL,
    conversation_id VARCHAR(255) NOT NULL,
    provider VARCHAR(64) NOT NULL,
    error_message TEXT NOT NULL,
    execution_time_ms BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_cognitive_analyses_character ON cognitive_analyses (character_id);
CREATE INDEX idx_cognitive_analyses_conversation ON cognitive_analyses (conversation_id);
CREATE INDEX idx_cognitive_analyses_created_at ON cognitive_analyses (created_at);
